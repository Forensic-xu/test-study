package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.AuthUser;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.common.PageResult;
import com.mall.admin.dto.OrderCreateRequest;
import com.mall.admin.entity.CartItem;
import com.mall.admin.entity.InventoryRecord;
import com.mall.admin.entity.Order;
import com.mall.admin.entity.OrderItem;
import com.mall.admin.entity.Product;
import com.mall.admin.enums.InventoryOperationType;
import com.mall.admin.enums.OrderStatus;
import com.mall.admin.enums.ProductStatus;
import com.mall.admin.enums.UserRole;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.CartMapper;
import com.mall.admin.mapper.InventoryRecordMapper;
import com.mall.admin.mapper.OrderItemMapper;
import com.mall.admin.mapper.OrderMapper;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.service.OrderService;
import com.mall.admin.vo.OrderItemVO;
import com.mall.admin.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Order creation runs in one DB transaction:
 * validate products → create order/items → conditional stock deduct → inventory records → remove cart lines.
 * Any failure rolls back all changes (including partial stock deducts).
 * <p>
 * Concurrency: stock uses {@code UPDATE ... WHERE stock >= qty}; cancel uses conditional status update
 * so repeated cancel cannot restore stock twice.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;
    private final InventoryRecordMapper inventoryRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(OrderCreateRequest request) {
        AuthUser user = AuthSupport.requireLogin();
        List<BuyLine> buyLines = resolveBuyLines(request, user.getUserId());
        if (buyLines.isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        // Merge same productId quantities (e.g. cart + direct overlap).
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (BuyLine line : buyLines) {
            merged.merge(line.productId(), line.quantity(), Integer::sum);
        }

        List<PreparedItem> preparedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : merged.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();
            if (quantity <= 0) {
                throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
            }

            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            if (product.getStatus() == null || product.getStatus() != ProductStatus.ON_SALE.getDbValue()) {
                throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
            }
            if (product.getStock() == null || product.getStock() < quantity) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT,
                        "商品库存不足: " + product.getName());
            }

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);
            preparedItems.add(new PreparedItem(product, quantity, price, subtotal));
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING.name());
        order.setRemark(request.getRemark());
        int inserted = orderMapper.insert(order);
        if (inserted != 1 || order.getId() == null) {
            throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED);
        }

        for (PreparedItem item : preparedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.product().getId());
            // Snapshot: name/price frozen at order time
            orderItem.setProductName(item.product().getName());
            orderItem.setProductPrice(item.price());
            orderItem.setQuantity(item.quantity());
            orderItem.setSubtotal(item.subtotal());
            orderItemMapper.insert(orderItem);

            int before = item.product().getStock();
            int affected = productMapper.decreaseStock(item.product().getId(), item.quantity());
            if (affected != 1) {
                throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT,
                        "商品库存不足: " + item.product().getName());
            }
            int after = before - item.quantity();
            saveInventoryRecord(item.product().getId(), before, -item.quantity(), after,
                    InventoryOperationType.ORDER_DEDUCT, user.getUserId(),
                    "订单扣减 orderNo=" + order.getOrderNo());
        }

        // Remove settled cart items (only those belonging to current user).
        removeSettledCartItems(request, user.getUserId(), merged.keySet());

        log.info("Order created: id={}, orderNo={}, userId={}, total={}",
                order.getId(), order.getOrderNo(), user.getUserId(), totalAmount);
        return getById(order.getId());
    }

    @Override
    public PageResult<OrderVO> page(long page, long size, String status) {
        AuthUser user = AuthSupport.requireLogin();
        long pageNo = page <= 0 ? 1 : page;
        long pageSize = size <= 0 ? 10 : Math.min(size, 100);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (!UserRole.ADMIN.name().equals(user.getRole())) {
            wrapper.eq(Order::getUserId, user.getUserId());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status.trim().toUpperCase());
        }
        wrapper.orderByDesc(Order::getId);

        Page<Order> result = orderMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        List<OrderVO> records = result.getRecords().stream()
                .map(o -> toVO(o, loadItems(o.getId())))
                .collect(Collectors.toList());

        return PageResult.<OrderVO>builder()
                .page(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .pages(result.getPages())
                .records(records)
                .build();
    }

    @Override
    public OrderVO getById(Long id) {
        AuthUser user = AuthSupport.requireLogin();
        Order order = requireOrder(id);
        assertCanView(user, order);
        return toVO(order, loadItems(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO cancel(Long id) {
        AuthUser user = AuthSupport.requireLogin();
        Order order = requireOrder(id);
        assertCanCancel(user, order);

        // Conditional update: only PENDING → CANCELLED succeeds (idempotent against double cancel).
        int affected = orderMapper.updateStatusIfMatch(id, OrderStatus.PENDING.name(), OrderStatus.CANCELLED.name());
        if (affected != 1) {
            Order latest = requireOrder(id);
            if (OrderStatus.CANCELLED.name().equals(latest.getStatus())) {
                throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED, "订单已取消，不能重复取消");
            }
            throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }

        List<OrderItem> items = loadItems(id);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            int before = product == null || product.getStock() == null ? 0 : product.getStock();
            productMapper.increaseStock(item.getProductId(), item.getQuantity());
            int after = before + item.getQuantity();
            saveInventoryRecord(item.getProductId(), before, item.getQuantity(), after,
                    InventoryOperationType.ORDER_CANCEL_RESTORE, user.getUserId(),
                    "取消订单恢复库存 orderId=" + id);
        }

        log.info("Order cancelled: id={}, userId={}", id, user.getUserId());
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO pay(Long id) {
        AuthUser admin = AuthSupport.requireAdmin();
        requireOrder(id);
        int affected = orderMapper.updateStatusIfMatch(id, OrderStatus.PENDING.name(), OrderStatus.PAID.name());
        if (affected != 1) {
            throw new BusinessException(ErrorCode.ORDER_PAY_NOT_ALLOWED);
        }
        log.info("Order paid: id={}, operator={}", id, admin.getUserId());
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO ship(Long id) {
        AuthUser admin = AuthSupport.requireAdmin();
        requireOrder(id);
        int affected = orderMapper.updateStatusIfMatch(id, OrderStatus.PAID.name(), OrderStatus.SHIPPED.name());
        if (affected != 1) {
            throw new BusinessException(ErrorCode.ORDER_SHIP_NOT_ALLOWED);
        }
        log.info("Order shipped: id={}, operator={}", id, admin.getUserId());
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO complete(Long id) {
        AuthUser admin = AuthSupport.requireAdmin();
        requireOrder(id);
        int affected = orderMapper.updateStatusIfMatch(id, OrderStatus.SHIPPED.name(), OrderStatus.COMPLETED.name());
        if (affected != 1) {
            throw new BusinessException(ErrorCode.ORDER_COMPLETE_NOT_ALLOWED);
        }
        log.info("Order completed: id={}, operator={}", id, admin.getUserId());
        return getById(id);
    }

    private List<BuyLine> resolveBuyLines(OrderCreateRequest request, Long userId) {
        boolean hasCartIds = !CollectionUtils.isEmpty(request.getCartItemIds());
        boolean checkoutAll = Boolean.TRUE.equals(request.getCheckoutAll());
        boolean hasItems = !CollectionUtils.isEmpty(request.getItems());

        if (!hasCartIds && !checkoutAll && !hasItems) {
            throw new BusinessException(ErrorCode.ORDER_MODE_INVALID);
        }

        List<BuyLine> lines = new ArrayList<>();

        if (checkoutAll) {
            List<CartItem> all = cartMapper.selectList(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId));
            for (CartItem cartItem : all) {
                lines.add(new BuyLine(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getId()));
            }
        } else if (hasCartIds) {
            for (Long cartId : request.getCartItemIds()) {
                CartItem cartItem = cartMapper.selectById(cartId);
                if (cartItem == null) {
                    throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
                }
                if (!Objects.equals(cartItem.getUserId(), userId)) {
                    throw new BusinessException(ErrorCode.CART_FORBIDDEN);
                }
                lines.add(new BuyLine(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getId()));
            }
        }

        if (hasItems) {
            for (OrderCreateRequest.OrderItemRequest item : request.getItems()) {
                lines.add(new BuyLine(item.getProductId(), item.getQuantity(), null));
            }
        }
        return lines;
    }

    private void removeSettledCartItems(OrderCreateRequest request, Long userId, java.util.Set<Long> productIds) {
        if (Boolean.TRUE.equals(request.getCheckoutAll())) {
            cartMapper.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
            return;
        }
        if (!CollectionUtils.isEmpty(request.getCartItemIds())) {
            cartMapper.delete(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .in(CartItem::getId, request.getCartItemIds()));
            return;
        }
        // Direct buy: also clear matching cart lines for purchased products (optional UX).
        // Spec: "购物车商品删除" for cart checkout; for direct buy we only remove if those products are in cart.
        if (!CollectionUtils.isEmpty(request.getItems()) && !productIds.isEmpty()) {
            cartMapper.delete(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, userId)
                    .in(CartItem::getProductId, productIds));
        }
    }

    private Order requireOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    private void assertCanView(AuthUser user, Order order) {
        if (UserRole.ADMIN.name().equals(user.getRole())) {
            return;
        }
        if (!Objects.equals(user.getUserId(), order.getUserId())) {
            throw new BusinessException(ErrorCode.ORDER_FORBIDDEN);
        }
    }

    private void assertCanCancel(AuthUser user, Order order) {
        if (UserRole.ADMIN.name().equals(user.getRole())) {
            return;
        }
        if (!Objects.equals(user.getUserId(), order.getUserId())) {
            throw new BusinessException(ErrorCode.ORDER_FORBIDDEN);
        }
    }

    private List<OrderItem> loadItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
                .orderByAsc(OrderItem::getId));
    }

    private void saveInventoryRecord(Long productId, int before, int change, int after,
                                     InventoryOperationType type, Long operatorId, String remark) {
        InventoryRecord record = new InventoryRecord();
        record.setProductId(productId);
        record.setBeforeStock(before);
        record.setChangeQuantity(change);
        record.setAfterStock(after);
        record.setOperationType(type.name());
        record.setOperatorId(operatorId);
        record.setRemark(remark);
        inventoryRecordMapper.insert(record);
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "ORD" + ts + rand;
    }

    private OrderVO toVO(Order order, List<OrderItem> items) {
        List<OrderItemVO> itemVOs = items.stream()
                .map(i -> OrderItemVO.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .productPrice(i.getProductPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .remark(order.getRemark())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemVOs)
                .build();
    }

    private record BuyLine(Long productId, Integer quantity, Long cartItemId) {
    }

    private record PreparedItem(Product product, int quantity, BigDecimal price, BigDecimal subtotal) {
    }
}

package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.admin.common.AuthSupport;
import com.mall.admin.common.AuthUser;
import com.mall.admin.common.ErrorCode;
import com.mall.admin.dto.CartAddRequest;
import com.mall.admin.dto.CartUpdateRequest;
import com.mall.admin.entity.CartItem;
import com.mall.admin.entity.Product;
import com.mall.admin.enums.ProductStatus;
import com.mall.admin.exception.BusinessException;
import com.mall.admin.mapper.CartMapper;
import com.mall.admin.mapper.ProductMapper;
import com.mall.admin.service.CartService;
import com.mall.admin.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartItemVO> listMyCart() {
        AuthUser user = AuthSupport.requireLogin();
        List<CartItem> items = cartMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, user.getUserId())
                .orderByDesc(CartItem::getId));
        Map<Long, Product> productMap = loadProducts(items);
        return items.stream()
                .map(item -> toVO(item, productMap.get(item.getProductId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItemVO add(CartAddRequest request) {
        AuthUser user = AuthSupport.requireLogin();
        Product product = requireOnSaleProduct(request.getProductId());
        validateQuantityAgainstStock(request.getQuantity(), product.getStock());

        CartItem existing = cartMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, user.getUserId())
                .eq(CartItem::getProductId, request.getProductId()));

        if (existing == null) {
            CartItem item = new CartItem();
            item.setUserId(user.getUserId());
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            cartMapper.insert(item);
            log.info("Cart item added: userId={}, productId={}, qty={}",
                    user.getUserId(), request.getProductId(), request.getQuantity());
            return toVO(item, product);
        }

        int newQty = existing.getQuantity() + request.getQuantity();
        validateQuantityAgainstStock(newQty, product.getStock());
        existing.setQuantity(newQty);
        cartMapper.updateById(existing);
        log.info("Cart item quantity updated(add): id={}, qty={}", existing.getId(), newQty);
        return toVO(existing, product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItemVO update(Long id, CartUpdateRequest request) {
        AuthUser user = AuthSupport.requireLogin();
        CartItem item = requireOwnCartItem(id, user.getUserId());
        Product product = requireOnSaleProduct(item.getProductId());
        validateQuantityAgainstStock(request.getQuantity(), product.getStock());

        item.setQuantity(request.getQuantity());
        cartMapper.updateById(item);
        log.info("Cart item updated: id={}, qty={}", id, request.getQuantity());
        return toVO(item, product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AuthUser user = AuthSupport.requireLogin();
        requireOwnCartItem(id, user.getUserId());
        cartMapper.deleteById(id);
        log.info("Cart item deleted: id={}, userId={}", id, user.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear() {
        AuthUser user = AuthSupport.requireLogin();
        cartMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, user.getUserId()));
        log.info("Cart cleared: userId={}", user.getUserId());
    }

    private CartItem requireOwnCartItem(Long id, Long userId) {
        CartItem item = cartMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        if (!Objects.equals(item.getUserId(), userId)) {
            // Hide other users' cart existence for isolation tests → treat as not found or forbidden.
            // Spec asks for clear unauthorized access: use 403.
            throw new BusinessException(ErrorCode.CART_FORBIDDEN);
        }
        return item;
    }

    private Product requireOnSaleProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (product.getStatus() == null || product.getStatus() != ProductStatus.ON_SALE.getDbValue()) {
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
        }
        return product;
    }

    private void validateQuantityAgainstStock(int quantity, Integer stock) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
        }
        if (stock == null || quantity > stock) {
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }
    }

    private Map<Long, Product> loadProducts(List<CartItem> items) {
        Set<Long> ids = items.stream().map(CartItem::getProductId).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return productMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
    }

    private CartItemVO toVO(CartItem item, Product product) {
        ProductStatus status = product == null ? null : ProductStatus.fromDb(product.getStatus());
        return CartItemVO.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .productId(item.getProductId())
                .productName(product == null ? null : product.getName())
                .productPrice(product == null ? null : product.getPrice())
                .productStock(product == null ? null : product.getStock())
                .productStatus(status == null ? null : status.name())
                .quantity(item.getQuantity())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}

package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * Concurrent-safe increase: atomic stock = stock + qty.
     */
    @Update("UPDATE products SET stock = stock + #{quantity}, updated_at = NOW() WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    /**
     * Concurrent-safe decrease: only succeeds when stock >= quantity.
     * Prevents two concurrent requests from overselling the same stock.
     */
    @Update("UPDATE products SET stock = stock - #{quantity}, updated_at = NOW() " +
            "WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}

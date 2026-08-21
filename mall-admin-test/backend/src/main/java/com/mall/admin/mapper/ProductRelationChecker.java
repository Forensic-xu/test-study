package com.mall.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductRelationChecker {

    @Select("SELECT COUNT(1) FROM cart WHERE product_id = #{productId}")
    long countCartByProductId(@Param("productId") Long productId);

    @Select("SELECT COUNT(1) FROM order_items WHERE product_id = #{productId}")
    long countOrderItemsByProductId(@Param("productId") Long productId);
}

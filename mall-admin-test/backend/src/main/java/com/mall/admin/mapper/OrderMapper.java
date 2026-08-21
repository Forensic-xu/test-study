package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * Conditional status transition for concurrency-safe / idempotent updates.
     * Returns 1 only when current status matches expectedFrom.
     */
    @Update("UPDATE orders SET status = #{toStatus}, updated_at = NOW() " +
            "WHERE id = #{orderId} AND status = #{fromStatus}")
    int updateStatusIfMatch(@Param("orderId") Long orderId,
                            @Param("fromStatus") String fromStatus,
                            @Param("toStatus") String toStatus);
}

package com.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.admin.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<CartItem> {
}

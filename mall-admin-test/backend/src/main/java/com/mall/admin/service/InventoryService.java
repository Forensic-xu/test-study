package com.mall.admin.service;

import com.mall.admin.dto.InventoryChangeRequest;
import com.mall.admin.vo.InventoryRecordVO;
import com.mall.admin.vo.InventoryVO;

import java.util.List;

public interface InventoryService {

    InventoryVO getByProductId(Long productId);

    InventoryVO increase(Long productId, InventoryChangeRequest request);

    InventoryVO decrease(Long productId, InventoryChangeRequest request);

    List<InventoryRecordVO> listRecords(Long productId);
}

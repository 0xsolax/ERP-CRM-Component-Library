package com.qmy.zhongsheng.core.production.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.production.ProductionBatchSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionDeliveryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderSaveDTO;
import com.qmy.zhongsheng.core.order.model.entity.OrdersDO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionGroupVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionOrderVO;

import java.util.List;

/**
 * 生产履约服务。
 *
 * @author AI Coding
 */
public interface ProductionService {

    Long saveGroup(ProductionGroupSaveDTO dto);

    Page<ProductionGroupVO> groupPage(ProductionGroupListQueryDTO query);

    ProductionGroupVO groupDetail(Long id);

    List<ProductionGroupVO> groupOptions(String keyword);

    Boolean deleteGroup(Long id);

    Page<ProductionOrderVO> orderPage(ProductionOrderListQueryDTO query);

    Long saveOrder(ProductionOrderSaveDTO dto);

    ProductionOrderVO orderDetail(Long id);

    ProductionOrderVO orderDetailByOrderId(Long orderId);

    ProductionOrderVO findMasterByOrderId(Long orderId);

    ProductionOrderVO ensureMasterForOrder(OrdersDO order);

    void syncPurchaseProgress(Long orderId);

    ProductionOrderVO arrangeBatches(ProductionBatchSaveDTO dto);

    ProductionOrderVO recordDelivery(ProductionDeliveryDTO dto);

    byte[] buildExportExcel(Long id);
}

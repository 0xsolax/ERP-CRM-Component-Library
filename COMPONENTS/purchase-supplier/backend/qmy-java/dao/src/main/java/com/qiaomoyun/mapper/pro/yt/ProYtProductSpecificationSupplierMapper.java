package com.qiaomoyun.mapper.pro.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;

import java.util.List;

public interface ProYtProductSpecificationSupplierMapper extends BaseMapper<ProYtProductSpecificationSupplier> {
    void deleteBySpecificationId(Long id);

    void saveBatch(List<ProYtProductSpecificationSupplier> supplierList);

    ProYtProductSpecificationSupplier selectSupplierInfoById(Long id);

    ProYtProductSpecificationSupplier selectBySpecificationIdAndSupplier(Long specificationId, Long supplierId);

    List<ProYtProductSpecificationSupplier> selectBySpecificationId(Long specificationId);

    List<ProYtProductSpecificationSupplier> listReplaceableSuppliers(List<Long> specificationIds);

    /**
     * 根据供应商ID获取其产品规格信息
     * @param supplierId 供应商ID
     * @return 供应商产品规格信息列表
     */
    List<ProYtProductSpecificationSupplier> selectBySupplierId(Long supplierId);

    /**
     * 根据供应商和规格信息获取供应商产品规格信息
     * @param proYtProductSpecificationSupplier 供应商产品规格信息
     * @return 供应商产品规格信息列表
     */
    List<ProYtProductSpecificationSupplier> selectBySpecificationSupplier(ProYtProductSpecificationSupplier proYtProductSpecificationSupplier);
}

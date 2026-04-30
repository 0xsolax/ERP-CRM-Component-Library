/*
 * @author java_deng
 * @date 2025/11/20 10:15
 * @description 客户规格映射Manager
 */
package com.qiaomoyun.manager.sal.yt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.entity.pro.yt.*;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerSpecificationComparison;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationItemMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerSpecificationComparisonMapper;
import com.qiaomoyun.param.pro.yt.ProYtProductQueryParams;
import com.qiaomoyun.param.sal.yt.SalYtCustomerSpecificationComparisonQueryParams;
import com.qiaomoyun.util.LoginUserInfoContext;
import com.qiaomoyun.vo.pro.yt.ProYtProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户规格映射Manager实现类
 */
@Service
public class SalYtCustomerSpecificationComparisonManager {

    @Autowired
    private SalYtCustomerSpecificationComparisonMapper salYtCustomerSpecificationComparisonMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private ProYtProductMapper proYtProductMapper;
    @Autowired
    private ProYtProductSpecificationMapper proYtProductSpecificationMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private ProYtProductSpecificationItemMapper proYtProductSpecificationItemMapper;

    /**
     * 新增或编辑客户规格映射
     */
    @Transactional
    public void createOrUpdate(SalYtCustomerSpecificationComparison entity) {
        // 检查规格是否已存在（针对同一客户）
        if (existsByName(entity.getCustomerId(), entity.getSpecification(), entity.getId())) {
            throw new RuntimeException("该规格已存在映射关系");
        }
        if (entity.getId() == null) {
            salYtCustomerSpecificationComparisonMapper.insert(entity);
        } else {
            salYtCustomerSpecificationComparisonMapper.updateById(entity);
        }
    }

    /**
     * 删除客户规格映射
     */
    @Transactional
    public void deleteById(Long id) {
        SalYtCustomerSpecificationComparison entity = new SalYtCustomerSpecificationComparison();
        entity.setId(id);
        entity.setIsDeleted(1);
        entity.setUpdateUser(LoginUserInfoContext.getUserId());
        entity.setUpdateTime(LocalDateTime.now());
        salYtCustomerSpecificationComparisonMapper.updateById(entity);
    }

    /**
     * 获取客户规格映射详情
     */
    public SalYtCustomerSpecificationComparison getById(Long id) {
        return salYtCustomerSpecificationComparisonMapper.selectById(id);
    }

    /**
     * 查询客户规格映射列表
     */
    public List<SalYtCustomerSpecificationComparison> list(SalYtCustomerSpecificationComparisonQueryParams queryParams) {
        List<SalYtCustomerSpecificationComparison> unmappedItems=null;

        //客户规格不为空就不需要查询未映射列表了，因为客户规格不为空，说明查询的必然是有客户规格映射的规格
        if(queryParams.getCustomerSpecification()==null){
            unmappedItems = salYtCustomerSpecificationComparisonMapper.selectUnmappedSpecificationItems(queryParams);
        }

        List<SalYtCustomerSpecificationComparison> mappedItems=salYtCustomerSpecificationComparisonMapper.list(queryParams);
        // 3. 合并两个列表
        List<SalYtCustomerSpecificationComparison> result = new ArrayList<>();
        result.addAll(mappedItems);
        if(unmappedItems!=null){
            result.addAll(unmappedItems);
        }

        return result;
    }

    public List<SalYtCustomerSpecificationComparison> mappedItems(SalYtCustomerSpecificationComparisonQueryParams queryParams) {

        return salYtCustomerSpecificationComparisonMapper.list(queryParams);

    }
    /**
     * 检查规格是否已存在
     */
    public boolean existsByName(Long customerId, String specification, Long excludeId) {
        return salYtCustomerSpecificationComparisonMapper.existsByCustomerAndSpecification(customerId, specification, excludeId);
    }

    /**
     * 批量删除客户的规格映射
     */
    @Transactional
    public void deleteByCustomerId(Long customerId) {
        salYtCustomerSpecificationComparisonMapper.deleteByCustomerId(customerId);
    }

    /**
     * 批量导入客户规格映射
     */
    @Transactional
    public void batchImport(List<SalYtCustomerSpecificationComparison> list) {
        if (list != null && !list.isEmpty()) {
            list.forEach(salYtCustomerSpecificationComparison -> {
                String specification = salYtCustomerSpecificationComparison.getSpecification();
                Long customerId = salYtCustomerSpecificationComparison.getCustomerId();
                if(!specification.isEmpty()){
                    SalYtCustomerSpecificationComparison old=salYtCustomerSpecificationComparisonMapper.selectBySpecificationAndCustomer(specification,customerId);
                    if(old!=null){
                        old.setCustomerSpecification(salYtCustomerSpecificationComparison.getCustomerSpecification());
                        salYtCustomerSpecificationComparisonMapper.updateById(old);
                    }else {
                        salYtCustomerSpecificationComparisonMapper.insert(salYtCustomerSpecificationComparison);
                    }
                }
            });
        }
    }

    /**
     * 根据客户ID和规格名称查询对应的客户规格
     */
    public String getCustomerSpecificationByCustomerIdAndSpecification(Long customerId, String specification) {
        QueryWrapper<SalYtCustomerSpecificationComparison> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", customerId);
        wrapper.eq("specification", specification);
        wrapper.eq("is_deleted", 0);

        SalYtCustomerSpecificationComparison entity = salYtCustomerSpecificationComparisonMapper.selectOne(wrapper);
        return entity != null ? entity.getCustomerSpecification() : null;
    }

    public Object getCustomerSpecification(SalYtCustomerSpecificationComparisonQueryParams queryParams) {
        // 先查询产品，需要规格名称
        ProYtProductQueryParams proYtProductQueryParams = new ProYtProductQueryParams();
        BeanUtils.copyProperties(queryParams, proYtProductQueryParams);
        proYtProductQueryParams.setCode(queryParams.getProductCode());
        proYtProductQueryParams.setId(queryParams.getProductId());
        PageHelper.startPage(queryParams.getPageNum(),queryParams.getPageSize());
        List<ProYtProductVo> productList=proYtProductMapper.selectCustomerProduct(proYtProductQueryParams);
        // 然后查询每个产品的规格进行填充
        productList.forEach(productVo->{
            Long id = productVo.getId();
            List<ProYtProductSpecification> proYtProductSpecifications = proYtProductSpecificationMapper.selectByProductId(id, null);

            if(proYtProductSpecifications!=null&& !proYtProductSpecifications.isEmpty()){
                proYtProductSpecifications.forEach(specification->{
                    //为每个规格设置图片
                    List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specification.getId(), ProductFilesTypeEnum.specification.getKey());
                    specification.setSpecificationImages(proYtProductFiles);

                    // 为每个规格设置规格项列表
                    List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectCustomerItemByProductSpecificationId(specification.getId(),queryParams.getCustomerId());
                    specification.setSpecificationItemList(specificationItems);

                    Long specificationId = specification.getId();
                    SalYtCustomerSpecificationComparison salYtCustomerSpecificationComparison = salYtCustomerSpecificationComparisonMapper.selectBySpecificationIdAndCustomerId(specificationId, queryParams.getCustomerId());
                    if(salYtCustomerSpecificationComparison!=null){
                        specification.setItemNumber(salYtCustomerSpecificationComparison.getItemNumber());
                    }
                });
            }
            productVo.setSpecifications(proYtProductSpecifications);
        });
        return new PageResultInfo<>(productList);
    }

    public void createOrUpdateItemNumber(SalYtCustomerSpecificationComparison entity) {
        Long specificationId = entity.getSpecificationId();
        Long customerId = entity.getCustomerId();
        SalYtCustomerSpecificationComparison customerSpecificationComparison = salYtCustomerSpecificationComparisonMapper.selectBySpecificationIdAndCustomerId(specificationId, customerId);
        if(customerSpecificationComparison!=null){
            customerSpecificationComparison.setItemNumber(entity.getItemNumber());
            salYtCustomerSpecificationComparisonMapper.updateById(customerSpecificationComparison);
        }else {
            salYtCustomerSpecificationComparisonMapper.insert(entity);
        }
    }
}
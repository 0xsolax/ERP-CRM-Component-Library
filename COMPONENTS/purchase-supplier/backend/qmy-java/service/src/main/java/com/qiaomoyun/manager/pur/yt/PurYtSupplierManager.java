/*
 * @author java_deng
 * @date 2025/11/11
 * @description 供应商管理类
 */
package com.qiaomoyun.manager.pur.yt;

import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.entity.pur.yt.PurYtSupplierFollow;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.eunm.yt.LabelTypeEnum;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductLabelManager;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationSupplierMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtSupplierFollowMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtSupplierMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtContactPersonMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtContactPersonPhoneMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtContactPersonSocialMapper;
import com.qiaomoyun.param.pur.yt.PurYtSupplierQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtSupplierSpecificationQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtSupplierUpdateParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.sal.yt.SalYtContactPersonSocial;

@Service
public class PurYtSupplierManager {

    @Autowired
    private PurYtSupplierMapper purYtSupplierMapper;

    @Autowired
    private SalYtContactPersonMapper salYtContactPersonMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private SalYtContactPersonSocialMapper salYtContactPersonSocialMapper;
    @Autowired
    private SalYtContactPersonPhoneMapper salYtContactPersonPhoneMapper;
    @Autowired
    private ProYtProductLabelManager proYtProductLabelManager;
    @Autowired
    private PurYtSupplierFollowMapper purYtSupplierFollowMapper;
    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;

    /**
     * 获取供应商下拉框列表
     */
    public List<PurYtSupplier> getSupplierDropdown() {
        return purYtSupplierMapper.selectForDropdown();
    }

    /**
     * 更新供应商信息
     * @param params 供应商更新参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(PurYtSupplierUpdateParams params) {
        // 检查供应商是否存在
        Long id = params.getId();
        if (id == null) {
            throw new IllegalArgumentException("供应商ID不能为空");
        }

        PurYtSupplier existingSupplier = purYtSupplierMapper.selectById(id);
        if (existingSupplier == null) {
            throw new IllegalArgumentException("供应商不存在");
        }

        // 更新供应商基本信息
        PurYtSupplier supplier = new PurYtSupplier();
        BeanUtils.copyProperties(params, supplier);
        purYtSupplierMapper.updateById(supplier);
    }

    /**
     * 根据ID获取供应商详情
     * @param id 供应商ID
     * @return 供应商信息
     */
    public PurYtSupplier detail(Long id) {
        PurYtSupplier supplier = purYtSupplierMapper.selectById(id);

        // 获取标签信息
        supplier.setLabelList(proYtProductLabelManager.selectByMasterIdAndType(id, LabelTypeEnum.supplierLabel.getKey()));

        //联系人
        List<SalYtContactPerson> contactPersonList = salYtContactPersonMapper.selectBySupplierId(id);
        contactPersonList.forEach(this::loadContactPersonDetails);
        supplier.setContactPersonList(contactPersonList);

        //跟进记录
        List<PurYtSupplierFollow> purYtSupplierFollows = purYtSupplierFollowMapper.selectBySupplierId(id);
        supplier.setFollowList(purYtSupplierFollows);
        purYtSupplierFollows.forEach(purYtSupplierFollow -> {
            Long followId = purYtSupplierFollow.getId();
            //附件
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(followId, ProductFilesTypeEnum.SupplierFollowFile.getKey());
            purYtSupplierFollow.setFileList(proYtProductFiles);
        });

        // 设置最近采购时间
        Date recentPurchaseTime = purYtSupplierMapper.selectRecentPurchaseTime(id);
        supplier.setRecentPurchaseTime(recentPurchaseTime);

        // 设置最近跟进时间
        Date recentFollowTime = purYtSupplierMapper.selectRecentFollowTime(id);
        supplier.setRecentFollowTime(recentFollowTime);

        return supplier;
    }

    /**
     * 给供应商添加标签
     * @param label 标签信息
     */
    public void addSupplierLabel(ProYtProductLabel label) {
        // 设置标签类型为供应商标签
        label.setType(LabelTypeEnum.supplierLabel.getKey());
        List<ProYtProductLabel> labelList = new ArrayList<>();
        labelList.add(label);
        proYtProductLabelManager.saveOrUpdateLabel(label.getMasterId(), labelList, LabelTypeEnum.supplierLabel.getKey());
    }

    /**
     * 删除供应商标签
     * @param labelId 标签ID
     */
    public void deleteSupplierLabel(Integer labelId) {
        proYtProductLabelManager.deleteLabelById(labelId);
    }

    /**
     * 批量添加供应商标签
     * @param supplierId 供应商ID
     * @param labels 标签列表
     */
    public void batchAddSupplierLabels(Long supplierId, List<ProYtProductLabel> labels) {
        proYtProductLabelManager.saveOrUpdateLabel(supplierId, labels, LabelTypeEnum.supplierLabel.getKey());
    }

    /**
     * 获取供应商标签列表
     * @param supplierId 供应商ID
     * @return 标签列表
     */
    public List<ProYtProductLabel> getSupplierLabels(Long supplierId) {
        return proYtProductLabelManager.selectByMasterIdAndType(supplierId, LabelTypeEnum.supplierLabel.getKey());
    }

    /**
     * 新增供应商
     * @param params 供应商新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSupplier(PurYtSupplierUpdateParams params) {
        // 设置供应商基本信息
        PurYtSupplier supplier = new PurYtSupplier();
        BeanUtils.copyProperties(params, supplier);
        String code = EntityCodeGenerateUtil.generateUniqueId("K");
        supplier.setCode(code);
        // 插入供应商
        purYtSupplierMapper.insert(supplier);

        //处理供应商联系人
        saveSupplierContactPerson(params, supplier.getId());
        //处理标签
        proYtProductLabelManager.saveOrUpdateLabel(supplier.getId(), params.getLabelList(), LabelTypeEnum.supplierLabel.getKey());
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveSupplierContactPerson(PurYtSupplierUpdateParams params, Long supplierId) {
        // 处理联系人
        List<SalYtContactPerson> contactPersonList = params.getContactPersonList();
        if (contactPersonList != null && !contactPersonList.isEmpty()) {
            // 设置联系人的供应商ID和其他信息
            for (SalYtContactPerson contact : contactPersonList) {
                contact.setSupplierId(supplierId);
                contact.setCustomerId(null); // 确保客户ID为null
                salYtContactPersonMapper.insert(contact);

                // 处理联系人头像
                proYtProductManager.handleProductFiles(contact.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey(), contact.getFileList());

                // 处理联系人社交账号信息（如果有）
                if (contact.getSocialList() != null && !contact.getSocialList().isEmpty()) {
                    for (SalYtContactPersonSocial social : contact.getSocialList()) {
                        social.setContactId(contact.getId());
                        salYtContactPersonSocialMapper.insert(social);
                    }
                }

                //处理联系电话
                List<SalYtContactPersonPhone> phoneList = contact.getPhoneList();
                if(phoneList != null && !phoneList.isEmpty()){
                    for (SalYtContactPersonPhone phone : phoneList) {
                        phone.setContactId(contact.getId());
                        salYtContactPersonPhoneMapper.insert(phone);
                    }
                }
            }
        }
    }

    /**
     * 更新供应商联系人
     * @param contactPerson 联系人信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplierContactPerson(SalYtContactPerson contactPerson) {
        // 更新联系人头像
        if (contactPerson.getFileList() != null) {
            // 先删除原有的头像
            proYtProductFileMapper.deleteByMasterIdAndType(contactPerson.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey());

            // 保存新的头像
            proYtProductManager.handleProductFiles(contactPerson.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey(), contactPerson.getFileList());
        }

        // 更新联系人社交账号
        // 处理联系人社交账号信息（如果有）
        if (contactPerson.getSocialList() != null && !contactPerson.getSocialList().isEmpty()) {
            for (SalYtContactPersonSocial social : contactPerson.getSocialList()) {
                social.setContactId(contactPerson.getId());
                if(social.getId()==null){
                    salYtContactPersonSocialMapper.insert(social);
                }else{
                    salYtContactPersonSocialMapper.updateById(social);
                }

            }
        }

        //处理联系电话
        List<SalYtContactPersonPhone> phoneList = contactPerson.getPhoneList();
        if(phoneList != null && !phoneList.isEmpty()){
            for (SalYtContactPersonPhone phone : phoneList) {
                phone.setContactId(contactPerson.getId());
                if(phone.getId()==null){
                    salYtContactPersonPhoneMapper.insert(phone);
                }else {
                    salYtContactPersonPhoneMapper.updateById(phone);
                }
            }
        }
    }

    /**
     * 获取供应商联系人列表
     * @param supplierId 供应商ID
     * @return 联系人列表
     */
    public List<SalYtContactPerson> getSupplierContactPersons(Long supplierId) {
        List<SalYtContactPerson> contactPersons = salYtContactPersonMapper.selectBySupplierId(supplierId);

        // 加载每个联系人的详情信息
        for (SalYtContactPerson contactPerson : contactPersons) {
            loadContactPersonDetails(contactPerson);
        }

        return contactPersons;
    }

    /**
     * 获取联系人详情
     * @param contactPersonId 联系人ID
     * @return 联系人信息
     */
    public SalYtContactPerson getContactPersonById(Long contactPersonId) {
        SalYtContactPerson contactPerson = salYtContactPersonMapper.selectById(contactPersonId);
        if (contactPerson != null) {
            loadContactPersonDetails(contactPerson);
        }
        return contactPerson;
    }

    /**
     * 加载联系人详情（头像、社交账号、联系电话）
     * @param contactPerson 联系人信息
     */
    private void loadContactPersonDetails(SalYtContactPerson contactPerson) {
        Long contactPersonId = contactPerson.getId();

        // 加载头像
        contactPerson.setFileList(proYtProductFileMapper.selectByMasterIdAndType(contactPersonId,ProductFilesTypeEnum.contactPersonProFile.getKey()));

        // 加载社交账号
        contactPerson.setSocialList(salYtContactPersonSocialMapper.selectByContactId(contactPersonId));

        // 加载联系电话
        contactPerson.setPhoneList(salYtContactPersonPhoneMapper.selectByContactId(contactPersonId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSupplierContactPerson(@Valid SalYtContactPerson contact) {
        if (contact.getId() != null) {
            // 编辑联系人
            salYtContactPersonMapper.updateById(contact);
            proYtProductFileMapper.deleteByMasterIdAndType(contact.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey());
        } else {
            // 新增联系人
            salYtContactPersonMapper.insert(contact);
        }
        updateSupplierContactPerson(contact);
    }

    public Object list(PurYtSupplierQueryParams params) {
        PageHelper.startPage(params.getPageNum(),params.getPageSize());
        List<PurYtSupplier> list = purYtSupplierMapper.list(params);
        list.forEach(supplier->{
            //标签
            Long id = supplier.getId();
            List<ProYtProductLabel> proYtProductLabels = proYtProductLabelManager.selectByMasterIdAndType(id, LabelTypeEnum.customerLabel.getKey());
            supplier.setLabelList(proYtProductLabels);
        });
        return new PageResultInfo<>(list);
    }

    public void deleteContactByContactId(Long id) {
        SalYtContactPerson salYtContactPerson = new SalYtContactPerson();
        salYtContactPerson.setId(id);
        salYtContactPerson.setIsDeleted(1);
        salYtContactPersonMapper.updateById(salYtContactPerson);
        salYtContactPersonPhoneMapper.deleteByContactId(id);
        salYtContactPersonSocialMapper.deleteByContactId(id);
    }

    public PageResultInfo<ProYtProductSpecificationSupplier> specificationList(PurYtSupplierSpecificationQueryParams params) {
        if(params.getSupplierId()==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        // 构建查询参数
        ProYtProductSpecificationSupplier query = new ProYtProductSpecificationSupplier();
        query.setSupplierId(params.getSupplierId());
        query.setProductCode(params.getProductCode());
        query.setSpecificationName(params.getSpecificationName());
        query.setSupplierSpecification(params.getSupplierSpecification());
        query.setSupplierSpecificationCode(params.getSupplierSpecificationCode());

        // 添加分页
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<ProYtProductSpecificationSupplier> supplierList = proYtProductSpecificationSupplierMapper.selectBySpecificationSupplier(query);
        for (ProYtProductSpecificationSupplier supplier : supplierList) {
            Long specificationId = supplier.getSpecificationId();
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            supplier.setImageList(fileListBySpecification);
            supplier.setItemList(itemsListBySpecification);
        }
        return new PageResultInfo<>(supplierList);
    }

    /**
     * 根据供应商规格ID获取详情
     * @param id 供应商规格ID
     * @return 供应商规格详情
     */
    public ProYtProductSpecificationSupplier getSpecificationSupplierDetail(Long id) {
        if(id == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        // 查询供应商规格信息
        ProYtProductSpecificationSupplier supplier = proYtProductSpecificationSupplierMapper.selectSupplierInfoById(id);
        if(supplier != null) {
            Long specificationId = supplier.getSpecificationId();
            // 加载产品图片列表
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            // 加载规格项列表
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            supplier.setImageList(fileListBySpecification);
            supplier.setItemList(itemsListBySpecification);
        }
        return supplier;
    }

    /**
     * 编辑供应商规格
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSpecificationSupplier(ProYtProductSpecificationSupplier supplierSpecification) {
        if(supplierSpecification == null || supplierSpecification.getId() == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        // 检查供应商规格是否存在
        ProYtProductSpecificationSupplier existingSupplier = proYtProductSpecificationSupplierMapper.selectById(supplierSpecification.getId());
        if(existingSupplier == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        //去除空格，增加代码健壮性
        if(supplierSpecification.getMinNumber()!=null){
            supplierSpecification.setMinNumber(supplierSpecification.getMinNumber().trim());
        }
        // 更新供应商规格信息
        proYtProductSpecificationSupplierMapper.updateById(supplierSpecification);
    }
}
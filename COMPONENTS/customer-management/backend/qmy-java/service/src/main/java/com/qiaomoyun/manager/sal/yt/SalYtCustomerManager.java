/*
 * @author java_deng
 * @date 2024/11/20 17:10
 * @description 客户管理实现类
 */
package com.qiaomoyun.manager.sal.yt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.entity.sys.*;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.eunm.yt.LabelTypeEnum;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.feishu.FeiShuManager;
import com.qiaomoyun.manager.pro.yt.ProYtProductLabelManager;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.manager.sys.SysDictionaryManager;
import com.qiaomoyun.mapper.sal.yt.*;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.sys.*;
import com.qiaomoyun.param.sal.yt.CustomerVipParams;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import com.qiaomoyun.param.sal.yt.SalYtCustomerUpdateParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.util.LoginUserInfoContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 客户管理实现类
 */
@Service
public class SalYtCustomerManager extends ServiceImpl<SalYtCustomerMapper, SalYtCustomer> {

    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;

    @Autowired
    private SalYtCustomerAddressMapper salYtCustomerAddressMapper;

    @Autowired
    private SalYtContactPersonMapper salYtContactPersonMapper;
    @Autowired
    private SalYtContactPersonSocialMapper salYtContactPersonSocialMapper;
    @Autowired
    private ProYtProductLabelManager proYtProductLabelManager;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private SalYtCustomerFollowMapper salYtCustomerFollowMapper;
    @Autowired
    private SalYtContactPersonPhoneMapper salYtContactPersonPhoneMapper;
    @Autowired
    private SysTenantMapper sysTenantMapper;
    @Autowired
    private SysTenantConfigMapper sysTenantConfigMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FeiShuManager feiShuManager;
    @Autowired
    private SysRegionMapper sysRegionMapper;
    @Autowired
    private SalYtOrderMapper salYtOrderMapper;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;
    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private SysUserDataPermissionMapper sysUserDataPermissionMapper;


    public Object list(SalYtCustomerQueryParams params){
        PageHelper.startPage(params.getPageNum(),params.getPageSize());
        List<SalYtCustomer> list = salYtCustomerMapper.list(params);
        //关闭数据权限校验，之后的sql不再校验数据权限
        LoginUserInfoContext.setIsOrganizeData(false);
        List<SysDictionary> autoCustomerLevel = sysDictionaryManager.getByCode(DictionaryConfigEnum.autoCustomerLevel.getKey());
        list.forEach(salYtCustomer->{
            //标签
            Long id = salYtCustomer.getId();
            List<ProYtProductLabel> proYtProductLabels = proYtProductLabelManager.selectByMasterIdAndType(id, LabelTypeEnum.customerLabel.getKey());
            salYtCustomer.setLabelList(proYtProductLabels);

            //最近跟进时间
            SalYtCustomerFollow follow = salYtCustomerFollowMapper.selectByCustomerIdFinally(id);
            if (follow != null) {
                salYtCustomer.setFollowTime(follow.getCreateTime());
            }
            
            // 近一年积累金额，最近下单时间字段填充
            BigDecimal yearOrderAmount = salYtOrderMapper.getYearOrderAmountByCustomerId(id);
            salYtCustomer.setYearOrderAmount(yearOrderAmount != null ? yearOrderAmount : BigDecimal.ZERO);
            
            LocalDateTime lastOrderTime = salYtOrderMapper.getLastOrderTimeByCustomerId(id);
            salYtCustomer.setLastOrderTime(lastOrderTime);

            //判断自动层级
            checkLevel(salYtCustomer,autoCustomerLevel);

        });


        return new PageResultInfo<>(list);
    }

    private void checkLevel(SalYtCustomer salYtCustomer, List<SysDictionary> autoCustomerLevel) {
        Boolean isA=false;
        Integer customerLevelMonthRangeA=null,customerLevelMonthRangeB=null;
        Integer customerLevelOrderNumberA=null,customerLevelOrderNumberB=null;
//        Integer customerLevelOrderProductNumberA=null,customerLevelOrderProductNumberB=null;
        BigDecimal customerLevelOrderAmountA=null,customerLevelOrderAmountB=null;
        for(SysDictionary dictionary : autoCustomerLevel){
            if(dictionary.getKey().equals("customerLevelOrderAmountA")){
                customerLevelOrderAmountA = new BigDecimal(dictionary.getValue());
            }
            if(dictionary.getKey().equals("customerLevelOrderAmountB")){
                customerLevelOrderAmountB = new BigDecimal(dictionary.getValue());
            }
            if(dictionary.getKey().equals("customerLevelMonthRangeA")){
                customerLevelMonthRangeA = Integer.parseInt(dictionary.getValue());
            }
            if(dictionary.getKey().equals("customerLevelMonthRangeB")){
                customerLevelMonthRangeB = Integer.parseInt(dictionary.getValue());
            }
            if(dictionary.getKey().equals("customerLevelOrderNumberA")){
                customerLevelOrderNumberA = Integer.parseInt(dictionary.getValue());
            }
            if(dictionary.getKey().equals("customerLevelOrderNumberB")){
                customerLevelOrderNumberB = Integer.parseInt(dictionary.getValue());
            }
//            if(dictionary.getKey().equals("customerLevelOrderProductNumberA")){
//                customerLevelOrderProductNumberA=Integer.parseInt(dictionary.getValue());
//            }
//            if(dictionary.getKey().equals("customerLevelOrderProductNumberB")){
//                customerLevelOrderProductNumberB=Integer.parseInt(dictionary.getValue());
//            }
        }

        //所有A级规则不为空才能进行判断
        if(customerLevelMonthRangeA!=null&& customerLevelOrderNumberA!=null && customerLevelOrderAmountA!=null){
            // 获取该客户从现在到过去productLevelMonthRangeA时间段内的订单单数和下单数量以及下单金额
            HashMap<String, Object> numberMap = salYtOrderSubItemMapper.countOrderAndItemByCustomerId(salYtCustomer.getId(), customerLevelMonthRangeA);
            Integer orderCount = numberMap.get("orderCount")!=null?Integer.parseInt(numberMap.get("orderCount").toString()):null;
//            Integer itemCount = numberMap.get("itemCount")!=null?Integer.parseInt(numberMap.get("itemCount").toString()):null;
            BigDecimal orderTotalAmount=numberMap.get("orderTotalPrice")!=null?new BigDecimal(numberMap.get("orderTotalPrice").toString()):null;
            if(orderCount!=null && orderTotalAmount!=null){
                if(orderCount>=customerLevelOrderNumberA && orderTotalAmount.compareTo(customerLevelOrderAmountA)>=0){
                    isA=true;
                }
            }
        }

        if(isA){
            salYtCustomer.setAutoCustomerLevel("A");
            return;
        }

        //2.不是A级则判断是否是B级
        //所有B级规则不为空才能进行判断
        if(customerLevelMonthRangeB!=null&& customerLevelOrderNumberB!=null && customerLevelOrderAmountB!=null){
            //获取该客户从现在到过去productLevelMonthRangeB时间段内的订单单数和下单金额
            HashMap<String, Object> numberMap = salYtOrderSubItemMapper.countOrderAndItemByCustomerId(salYtCustomer.getId(), customerLevelMonthRangeB);
            Integer orderCount = numberMap.get("orderCount")!=null?Integer.parseInt(numberMap.get("orderCount").toString()):null;
            BigDecimal orderTotalAmount=numberMap.get("orderTotalPrice")!=null?new BigDecimal(numberMap.get("orderTotalPrice").toString()):null;

            if(orderCount!=null && orderTotalAmount!=null){
                if(orderCount>=customerLevelOrderNumberB && orderTotalAmount.compareTo(customerLevelOrderAmountB)>=0){
                    salYtCustomer.setAutoCustomerLevel("B");
                }
            }
        }
    }

    /**
     * 给客户添加标签
     * @param label 标签信息
     */
    public void addLabel(ProYtProductLabel label) {
        // 设置标签类型为客户
        label.setType(LabelTypeEnum.customerLabel.getKey());
        List<ProYtProductLabel> list=new ArrayList<>();
        list.add(label);
        proYtProductLabelManager.saveOrUpdateLabel(label.getMasterId(),list,LabelTypeEnum.customerLabel.getKey());
    }

    /**
     * 删除客户标签
     * @param labelId 标签ID
     */
    public void deleteLabel(Integer labelId) {
        proYtProductLabelManager.deleteLabelById(labelId);
    }

    /**
     * 保存或更新客户跟进记录
     * @param follow 跟进记录
     */
    public void saveOrUpdateFollow(SalYtCustomerFollow follow) {
        if (follow.getId() == null) {
            // 新增记录
            salYtCustomerFollowMapper.insert(follow);
        } else {
            // 更新记录
            proYtProductFileMapper.deleteByMasterIdAndType(follow.getId(), ProductFilesTypeEnum.CustomerFollowFile.getKey());
            salYtCustomerFollowMapper.updateById(follow);
        }
        proYtProductManager.handleProductFiles(follow.getId(), ProductFilesTypeEnum.CustomerFollowFile.getKey(), follow.getFileList());
    }

    /**
     * 根据客户ID获取跟进记录列表
     * @param customerId 客户ID
     * @return 跟进记录列表
     */
    public List<SalYtCustomerFollow> getCustomerFollows(Long customerId) {
        return salYtCustomerFollowMapper.selectByCustomerId(customerId);
    }

    /**
     * 新增客户（包含地址和联系人）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveCustomer(SalYtCustomerUpdateParams customerUpdateParams) {
        SalYtCustomer customer = new SalYtCustomer();
        BeanUtils.copyProperties(customerUpdateParams, customer);
        String code = EntityCodeGenerateUtil.generateUniqueId("K");
        customer.setCode(code);
        // 保存客户主表
        this.save(customer);

        // 保存地址信息（批量插入）
        saveCustomerAddress(customerUpdateParams, customer.getId());

        // 保存联系人信息
        saveCustomerContactPerson(customerUpdateParams, customer.getId());

        //处理标签
        proYtProductLabelManager.saveOrUpdateLabel(customer.getId(), customerUpdateParams.getLabelList(), LabelTypeEnum.customerLabel.getKey());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCustomerContactPerson(SalYtCustomerUpdateParams params, Long customerId) {
        List<SalYtContactPerson> contactPersonList = params.getContactPersonList();
        if (contactPersonList != null && !contactPersonList.isEmpty()) {
            for (SalYtContactPerson contact : contactPersonList) {
                contact.setCustomerId(customerId);
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

    @Transactional(rollbackFor = Exception.class)
    public void saveCustomerAddress(SalYtCustomerUpdateParams params, Long customerId) {
        List<SalYtCustomerAddress> addressList = params.getCustomerAddressList();
        if (addressList != null && !addressList.isEmpty()) {
            for (SalYtCustomerAddress address : addressList) {
                address.setCustomerId(customerId);
            }
            salYtCustomerAddressMapper.insertBatch(addressList);
        }else{
            //添加默认地址
            SalYtCustomerAddress address = new SalYtCustomerAddress();
            address.setCustomerId(customerId);
            //默认收货人
            address.setConsignee("-");
            //默认联系方式
            address.setPhone("-");
            //默认国家地区名称
            address.setCountryRegion("-");
            //默认国家地区ID
            address.setCountryRegionId(-1L);
            //详细地址
            address.setDetail("-");
            //省
            address.setProvince("0");
            //市
            address.setCity("0");
            //县
            address.setCounty("0");
            salYtCustomerAddressMapper.insert(address);
        }
    }

    /**
     * 修改客户主表信息
     *
     * @param customer 客户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(SalYtCustomer customer) {
        LambdaUpdateWrapper<SalYtCustomer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalYtCustomer::getId, customer.getId());
        updateWrapper.set(SalYtCustomer::getBelongEmployeeId, customer.getBelongEmployeeId());
        updateWrapper.set(SalYtCustomer::getFollowEmployeeId, customer.getFollowEmployeeId());
        salYtCustomerMapper.update(customer, updateWrapper);
    }

    /**
     * 修改客户地址信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerAddress(SalYtCustomerAddress params) {
        if (params.getId() != null) {
            // 编辑地址
            salYtCustomerAddressMapper.updateById(params);
        } else {
            // 新增地址
            salYtCustomerAddressMapper.insert(params);
        }
    }

    /**
     * 修改客户联系人信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerContactPerson(SalYtContactPerson contact) {
        if (contact.getId() != null) {
            // 编辑联系人
            salYtContactPersonMapper.updateById(contact);
            proYtProductFileMapper.deleteByMasterIdAndType(contact.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey());
        } else {
            // 新增联系人
            salYtContactPersonMapper.insert(contact);
        }

        // 处理联系人头像
        proYtProductManager.handleProductFiles(contact.getId(), ProductFilesTypeEnum.contactPersonProFile.getKey(), contact.getFileList());

        // 处理联系人社交账号信息（如果有）
        if (contact.getSocialList() != null && !contact.getSocialList().isEmpty()) {
            for (SalYtContactPersonSocial social : contact.getSocialList()) {
                social.setContactId(contact.getId());
                if(social.getId()==null){
                    salYtContactPersonSocialMapper.insert(social);
                }else{
                    salYtContactPersonSocialMapper.updateById(social);
                }

            }
        }

        //处理联系电话
        List<SalYtContactPersonPhone> phoneList = contact.getPhoneList();
        if(phoneList != null && !phoneList.isEmpty()){
            for (SalYtContactPersonPhone phone : phoneList) {
                phone.setContactId(contact.getId());
                if(phone.getId()==null){
                    salYtContactPersonPhoneMapper.insert(phone);
                }else {
                    salYtContactPersonPhoneMapper.updateById(phone);
                }
            }
        }
    }

    /**
     * 删除客户联系人
     * @param contactId 联系人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContactPerson(Long contactId) {
        // 删除联系人主表（逻辑删除）
        salYtContactPersonMapper.deleteById(contactId);
        
        // 删除联系人社交账号信息
//        salYtContactPersonSocialMapper.deleteByContactId(contactId);
        
        // 删除联系人电话号码
//        salYtContactPersonPhoneMapper.deleteByContactId(contactId.intValue());
    }
    
    /**
     * 删除客户收货地址
     * @param addressId 收货地址ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerAddress(Long addressId) {
        // 逻辑删除收货地址
        salYtCustomerAddressMapper.deleteById(addressId);
    }
    
    /**
     * 删除客户跟进记录
     * @param followId 跟进记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerFollow(Long followId) {
        // 逻辑删除跟进记录
        salYtCustomerFollowMapper.deleteById(followId);
    }


    /**
     * 根据ID查询客户详情（包含地址和联系人）
     *
     * @param id 客户ID
     * @return 客户信息Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCustomerDetail(Long id) {
        //关闭数据权限：必须在 selectById 之前执行，否则 TenantInterceptor 会追加数据权限过滤条件，
        //导致归属人/跟进人不是当前用户的客户查询返回 null，误报"客户不存在"
        LoginUserInfoContext.setIsOrganizeData(false);
        // 查询客户主表信息
        SalYtCustomer customer = salYtCustomerMapper.selectById(id);
        if (customer == null) {
            return null;
        }
        //填充最近下单时间
        LocalDateTime lastOrderTime = salYtOrderMapper.getLastOrderTimeByCustomerId(id);
        customer.setLastOrderTime(lastOrderTime);

        //填充最近跟进时间
        SalYtCustomerFollow follow = salYtCustomerFollowMapper.selectByCustomerIdFinally(id);
        if (follow != null) {
            customer.setFollowTime(follow.getCreateTime());
        }

        //查询标签信息
        List<ProYtProductLabel> proYtProductLabels = proYtProductLabelManager.selectByMasterIdAndType(id, LabelTypeEnum.customerLabel.getKey());

        // 查询地址列表
        List<SalYtCustomerAddress> addressList = salYtCustomerAddressMapper.selectByCustomerIdExcludeDefault(id);

        //填充省市区id
        addressList.forEach(address -> {
            Long regionId = address.getRegionId();
            address.setCountyId(regionId);
            SysRegion county = sysRegionMapper.selectById(regionId);
            if (county != null) {
                Long cityId = county.getRegionParentId();
                address.setCityId(cityId);

                SysRegion city = sysRegionMapper.selectById(cityId);
                if (city != null) {
                    Long provinceId = city.getRegionParentId();
                    address.setProvinceId(provinceId);
                    if(provinceId<0){
                        address.setProvinceId(cityId);
                        address.setCityId(regionId);
                    }
                }
            }
        });
        // 查询联系人列表
        List<SalYtContactPerson> contactPersonList = salYtContactPersonMapper.selectByCustomerId(id);

        //联系人头像和社交方式
        contactPersonList.forEach(proYtContactPerson -> {
            Long contactPersonId = proYtContactPerson.getId();

            //头像
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(contactPersonId, ProductFilesTypeEnum.contactPersonProFile.getKey());
            proYtContactPerson.setFileList(proYtProductFiles);

            //社交平台
            List<SalYtContactPersonSocial> salYtContactPersonSocials = salYtContactPersonSocialMapper.selectByContactId(contactPersonId);
            proYtContactPerson.setSocialList(salYtContactPersonSocials);

            //联系电话
            List<SalYtContactPersonPhone> salYtContactPersonPhones = salYtContactPersonPhoneMapper.selectByContactId(contactPersonId);
            proYtContactPerson.setPhoneList(salYtContactPersonPhones);
        });

        // 获取并添加跟进记录
        List<SalYtCustomerFollow> follows = salYtCustomerFollowMapper.selectByCustomerId(id);
        follows.forEach(proYtCustomerFollow -> {
            Long followId = proYtCustomerFollow.getId();
            //附件
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(followId, ProductFilesTypeEnum.CustomerFollowFile.getKey());
            proYtCustomerFollow.setFileList(proYtProductFiles);
        });
        // 组装结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("customer", customer);
        result.put("labelList", proYtProductLabels);
        result.put("addressList", addressList);
        result.put("followList", follows);
        result.put("contactPersonList", contactPersonList);

        return result;
    }

    /**
     * 删除客户（级联删除地址和联系人）
     *
     * @param id 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        // 删除客户主表（逻辑删除）
        this.removeById(id);

        // 删除客户地址（逻辑删除）
        salYtCustomerAddressMapper.deleteByCustomerId(id);

        // 删除客户联系人（逻辑删除）
        salYtContactPersonMapper.deleteByCustomerId(id);
    }

    /**
     * 批量删除客户
     *
     * @param ids 客户ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchCustomer(List<Long> ids) {
        for (Long id : ids) {
            this.deleteCustomer(id);
        }
    }

    public void enableStore(Long customerId) {

        // 获取客户信息
        SalYtCustomer customer = this.getById(customerId);

        //构建消息
        String message = String.format("请前去复核【客户编号：%s 客户名称：%s】的开启独立仓请求",
                customer.getCode(),
                customer.getName(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 发送飞书消息
        feiShuManager.sengMessageToSuperAdmin(message);

        //修改客户独立仓状态
        customer.setStoreStatus(1);//待审核
        customer.setStoreOperationTime(LocalDateTime.now());
        customer.setStoreOperationUser(LoginUserInfoContext.getUserId());
        salYtCustomerMapper.updateById(customer);

    }
    
    /**
     * 独立仓审核
     * @param customerId 客户ID
     * @param auditResult 审核结果 2-通过 3-拒绝
     */
    public void auditStore(Long customerId, Integer auditResult) {
        SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);
        if (customer == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(),"客户不存在");
        }
        
        //更新独立仓状态
        customer.setStoreStatus(auditResult);
        customer.setStoreOperationTime(LocalDateTime.now());
        customer.setStoreOperationUser(LoginUserInfoContext.getUserId());
        salYtCustomerMapper.updateById(customer);
    }

    public Object getCustomerAddress(Long id) {
        return salYtCustomerAddressMapper.selectByCustomerId(id);
    }
    
    /**
     * 查询VIP客户名单
     * @return VIP客户和非VIP客户名单
     */
    public Map<String, List<SalYtCustomer>> getVipCustomerList(CustomerVipParams params) {
        // 查询VIP客户列表
        List<SalYtCustomer> vipList = salYtCustomerMapper.selectVipCustomers(params);
        
        // 查询非VIP客户列表
        List<SalYtCustomer> nonVipList = salYtCustomerMapper.selectNonVipCustomers(params);
        
        // 组装结果
        Map<String, List<SalYtCustomer>> result = new HashMap<>();
        result.put("vipList", vipList);
        result.put("nonVipList", nonVipList);
        
        return result;
    }
    
    /**
     * 设置VIP客户
     * @param addVipIds 新增VIP客户的ID列表
     * @param removeVipIds 移除VIP客户的ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void setVipCustomers(List<Long> addVipIds, List<Long> removeVipIds) {
        // 新增VIP客户
        if (addVipIds != null && !addVipIds.isEmpty()) {
            for (Long id : addVipIds) {
                SalYtCustomer customer = new SalYtCustomer();
                customer.setId(id);
                customer.setIsVip(true);
                salYtCustomerMapper.updateById(customer);
            }
        }
        
        // 移除VIP客户
        if (removeVipIds != null && !removeVipIds.isEmpty()) {
            for (Long id : removeVipIds) {
                SalYtCustomer customer = new SalYtCustomer();
                customer.setId(id);
                customer.setIsVip(false);
                salYtCustomerMapper.updateById(customer);
            }
        }
    }

    public Object selectList(SalYtCustomerQueryParams params) {
        return salYtCustomerMapper.list(params);
    }

    public void setAutoLevel(Map<String, Integer> params) {
        List<SysDictionary> dictionaryList = sysDictionaryManager.getByCode(DictionaryConfigEnum.autoCustomerLevel.getKey());
        Integer customerLevelOrderAmountA = params.get("customerLevelOrderAmountA");
        Integer customerLevelOrderProductNumberA = params.get("customerLevelOrderProductNumberA");
        Integer customerLevelOrderNumberA = params.get("customerLevelOrderNumberA");
        Integer customerLevelMonthRangeA = params.get("customerLevelMonthRangeA");
        Integer customerLevelOrderAmountB = params.get("customerLevelOrderAmountB");
        Integer customerLevelOrderProductNumberB = params.get("customerLevelOrderProductNumberB");
        Integer customerLevelOrderNumberB = params.get("customerLevelOrderNumberB");
        Integer customerLevelMonthRangeB = params.get("customerLevelMonthRangeB");
        for (SysDictionary sysDictionary : dictionaryList) {
            String key = sysDictionary.getKey();
            if (key.equals("customerLevelOrderAmountA")) {
                if (customerLevelOrderAmountA != null) {
                    sysDictionary.setValue(customerLevelOrderAmountA.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelOrderProductNumberA")) {
                if (customerLevelOrderProductNumberA != null) {
                    sysDictionary.setValue(customerLevelOrderProductNumberA.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelOrderNumberA")) {
                if (customerLevelOrderNumberA != null) {
                    sysDictionary.setValue(customerLevelOrderNumberA.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelMonthRangeA")) {
                if (customerLevelMonthRangeA != null) {
                    sysDictionary.setValue(customerLevelMonthRangeA.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelOrderAmountB")) {
                if (customerLevelOrderAmountB != null) {
                    sysDictionary.setValue(customerLevelOrderAmountB.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelOrderProductNumberB")) {
                if (customerLevelOrderProductNumberB != null) {
                    sysDictionary.setValue(customerLevelOrderProductNumberB.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelOrderNumberB")) {
                if (customerLevelOrderNumberB != null) {
                    sysDictionary.setValue(customerLevelOrderNumberB.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                    continue;
                }
            }
            if (key.equals("customerLevelMonthRangeB")) {
                if (customerLevelMonthRangeB != null) {
                    sysDictionary.setValue(customerLevelMonthRangeB.toString());
                    sysDictionaryMapper.updateById(sysDictionary);
                }
            }
        }
    }

    public Object getConsumptionTrends(SalYtCustomerQueryParams params) {
        Long customerId = params.getCustomerId();
        if (customerId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 处理日期参数，默认过去6个月
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (params.getStartTime() != null) {
            startTime = params.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // 默认开始时间为6个月前
            startTime = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        if (params.getEndTime() != null) {
            endTime = params.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // 默认结束时间为当前时间
            endTime = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusNanos(1);
        }

//        // 生成月份标签列表
        List<String> xAxis = new ArrayList<>();

        LocalDateTime currentMonth = startTime.withDayOfMonth(1);
        while (!currentMonth.isAfter(endTime)) {
            xAxis.add(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            currentMonth = currentMonth.plusMonths(1);
        }

        // 查询实际的月度消费数据
        List<Map<String, Object>> consumptionData = salYtOrderMapper.getMonthlyConsumptionByCustomerId(customerId, startTime, endTime);

        // 将查询结果转换为Map，便于查找
        Map<String, BigDecimal> consumptionMap = new HashMap<>();
        for (Map<String, Object> data : consumptionData) {
            String month = (String) data.get("month");
            BigDecimal amount = (BigDecimal) data.get("amount");
            consumptionMap.put(month, amount);
        }

        // 构建Y轴数据，确保每个月份都有对应的数据点
        List<BigDecimal> series = new ArrayList<>();
        for (String month : xAxis) {
            series.add(consumptionMap.getOrDefault(month, BigDecimal.ZERO));
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("xAxis", xAxis);
        result.put("series", series);

        return result;
    }

    public Object getConsumptionRatio(SalYtCustomerQueryParams params) {
        Long customerId = params.getCustomerId();
        if (customerId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 处理日期参数，默认过去6个月
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (params.getStartTime() != null) {
            startTime = params.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if (params.getEndTime() != null) {
            endTime = params.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        // 查询实际的消费占比数据
        List<Map<String, Object>> consumptionData = salYtOrderMapper.getConsumptionRatioByCustomerId(customerId, startTime, endTime);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<BigDecimal> series = new ArrayList<>();

        for (Map<String, Object> data : consumptionData) {
            String categoryName = (String) data.get("categoryName");
            BigDecimal amount = (BigDecimal) data.get("amount");
            xAxis.add(categoryName);
            series.add(amount);
        }

        result.put("xAxis", xAxis);
        result.put("series", series);

        return result;
    }

    /**
     * 校验新增客户的联系人信息
     * @param params 客户更新参数
     * @return 校验结果，包含AgreeSubmit字段（success/warn/danger）
     */
    public Map<String, String> validateCustomerContact(SalYtCustomerUpdateParams params) {
        Map<String, String> result = new HashMap<>();
        String agreeSubmit = "success";
        String message = "";

        List<SalYtContactPerson> contactPersonList = params.getContactPersonList();
        if (contactPersonList != null && !contactPersonList.isEmpty()) {
            for (SalYtContactPerson contactPerson : contactPersonList) {
                Long contactId = contactPerson.getId();
                String name = contactPerson.getName();
                String email = contactPerson.getEmail();
                List<SalYtContactPersonPhone> phoneList = contactPerson.getPhoneList();

                // 检查相同姓名
                if (name != null && !name.isEmpty()) {
                    int nameCount = salYtContactPersonMapper.countByNameExcludeContactId(name, contactId);
                    if (nameCount > 0) {
                        agreeSubmit = "warn";
                        message = "已有相同姓名的联系人";
                    }
                }

                // 检查相同邮箱
                if (email != null && !email.isEmpty()) {
                    int emailCount = salYtContactPersonMapper.countByEmailExcludeContactId(email, contactId);
                    if (emailCount > 0) {
                        agreeSubmit = "danger";
                        message = "联系人已被占用，请勿重复添加";
                        break;
                    }
                }

                // 检查相同电话
                if (phoneList != null && !phoneList.isEmpty()) {
                    for (SalYtContactPersonPhone phone : phoneList) {
                        String phoneNumber = phone.getPhone();
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            int phoneCount = salYtContactPersonPhoneMapper.countByPhoneExcludeContactId(phoneNumber, contactId);
                            if (phoneCount > 0) {
                                agreeSubmit = "danger";
                                message = "联系人已被占用，请勿重复添加";
                                break;
                            }
                        }
                    }
                }

                // 如果已经是danger级别，直接跳出循环
                if ("danger".equals(agreeSubmit)) {
                    break;
                }
            }
        }

        result.put("agreeSubmit", agreeSubmit);
        result.put("message", message);
        return result;
    }

    public Object getCustomerAddressByCustomerId(Long id) {
        //判断用户是否有除了默认地址之外的地址
        List<SalYtCustomerAddress> customerAddressList = salYtCustomerAddressMapper.selectByCustomerIdExcludeDefault(id);
        if(!customerAddressList.isEmpty()){
            //有除默认地址的，返回有的
            return customerAddressList;
        }
        //没有除默认地址的，返回默认地址
        return salYtCustomerAddressMapper.selectByCustomerId(id);
    }
}
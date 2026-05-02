/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 物流公司管理类
 */
package com.qiaomoyun.manager.sto.yt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.sto.yt.StoYtTransportCompany;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.mapper.sto.yt.StoYtTransportCompanyMapper;
import com.qiaomoyun.param.sto.yt.StoYtTransportCompanyQueryParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物流公司管理类
 */
@Service
public class StoYtTransportCompanyManager {

    @Autowired
    private StoYtTransportCompanyMapper stoYtTransportCompanyMapper;

    /**
     * 新增物流公司
     * @param company 物流公司信息
     */
    public void addTransportCompany(StoYtTransportCompany company) {
        company.setCode(EntityCodeGenerateUtil.generateUniqueId("W"));
        stoYtTransportCompanyMapper.insert(company);
    }

    /**
     * 更新物流公司
     * @param company 物流公司信息
     */
    public void updateTransportCompany(StoYtTransportCompany company) {
        // 检查是否存在
        StoYtTransportCompany existingCompany = stoYtTransportCompanyMapper.selectById(company.getId());
        if (existingCompany == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        stoYtTransportCompanyMapper.updateById(company);
    }

    /**
     * 删除物流公司（逻辑删除）
     * @param id 物流公司ID
     */
    public void deleteTransportCompany(Long id) {
        // 检查是否存在
        StoYtTransportCompany company = stoYtTransportCompanyMapper.selectById(id);
        if (company == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 逻辑删除
        company.setIsDeleted(1);
        stoYtTransportCompanyMapper.updateById(company);
    }

    /**
     * 根据ID查询物流公司
     * @param id 物流公司ID
     * @return 物流公司信息
     */
    public StoYtTransportCompany getTransportCompanyById(Long id) {
        return stoYtTransportCompanyMapper.selectById(id);
    }

    /**
     * 查询所有物流公司
     * @return 物流公司列表
     */
    public List<StoYtTransportCompany> getAllTransportCompanies() {
        LambdaQueryWrapper<StoYtTransportCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoYtTransportCompany::getIsDeleted, 0);
        return stoYtTransportCompanyMapper.selectList(queryWrapper);
    }

    /**
     * 分页查询物流公司
     * @param params 查询参数
     * @return 分页结果
     */
    public PageResultInfo<StoYtTransportCompany> getTransportCompaniesByPage(StoYtTransportCompanyQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        LambdaQueryWrapper<StoYtTransportCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoYtTransportCompany::getIsDeleted, 0);

        // 添加查询条件
        if (params.getCode() != null && !params.getCode().isEmpty()) {
            queryWrapper.like(StoYtTransportCompany::getCode, params.getCode());
        }
        if (params.getName() != null && !params.getName().isEmpty()) {
            queryWrapper.like(StoYtTransportCompany::getName, params.getName());
        }
        if (params.getType() != null && !params.getType().isEmpty()) {
            queryWrapper.like(StoYtTransportCompany::getType, params.getType());
        }
        if (params.getIsHomeService() != null) {
            queryWrapper.eq(StoYtTransportCompany::getIsHomeService, params.getIsHomeService());
        }

        // 日期新的出现在前面
        queryWrapper.orderByDesc(StoYtTransportCompany::getCreateTime);

        List<StoYtTransportCompany> list = stoYtTransportCompanyMapper.selectList(queryWrapper);
        return new PageResultInfo<>(list);
    }
}

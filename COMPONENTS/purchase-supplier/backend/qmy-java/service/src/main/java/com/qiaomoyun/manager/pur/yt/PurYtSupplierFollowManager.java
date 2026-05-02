package com.qiaomoyun.manager.pur.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pur.yt.PurYtSupplierFollow;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtSupplierFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 供应商跟进Manager类
 * 处理供应商跟进记录的业务逻辑
 */
@Component
public class PurYtSupplierFollowManager {

    @Autowired
    private PurYtSupplierFollowMapper purYtSupplierFollowMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;

    @Autowired
    private ProYtProductManager proYtProductManager;

    /**
     * 添加供应商跟进记录
     * @param follow 供应商跟进信息
     */
    public void addFollow(PurYtSupplierFollow follow) {
        if (follow == null || follow.getSupplierId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        purYtSupplierFollowMapper.insert(follow);
        saveFile(follow);
    }

    /**
     * 更新供应商跟进记录
     * @param follow 供应商跟进信息
     */
    public void updateFollow(PurYtSupplierFollow follow) {
        purYtSupplierFollowMapper.updateById(follow);
        proYtProductFileMapper.deleteByMasterIdAndType(follow.getId(), ProductFilesTypeEnum.CustomerFollowFile.getKey());
        saveFile(follow);
    }

    private void saveFile(PurYtSupplierFollow follow){
        List<ProYtProductFile> fileList = follow.getFileList();
        proYtProductManager.handleProductFiles(follow.getId(), ProductFilesTypeEnum.SupplierFollowFile.getKey(),fileList);
    }

    /**
     * 删除供应商跟进记录（逻辑删除）
     * @param id 跟进记录ID
     */
    public void deleteFollow(Long id) {
        if (id == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        // 设置当前用户为更新者
        purYtSupplierFollowMapper.deleteById(id);
    }

    /**
     * 根据供应商ID查询跟进记录列表
     * @param supplierId 供应商ID
     * @return 跟进记录列表
     */
    public List<PurYtSupplierFollow> getFollowListBySupplierId(Long supplierId) {
        if (supplierId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return purYtSupplierFollowMapper.selectBySupplierId(supplierId);
    }

    /**
     * 根据ID查询跟进记录详情
     * @param id 跟进记录ID
     * @return 跟进记录详情
     */
    public PurYtSupplierFollow getFollowById(Long id) {
        if (id == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return purYtSupplierFollowMapper.selectById(id);
    }
}

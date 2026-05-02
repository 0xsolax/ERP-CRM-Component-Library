/*
 * @author java_deng
 * @date 2025/11/11
 * @description 库位管理类
 */

package com.qiaomoyun.manager.sto.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.sto.yt.StoYtLocation;
import com.qiaomoyun.mapper.sto.yt.StoYtLocationMapper;
import com.qiaomoyun.util.ValidityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoYtLocationManager {

    @Autowired
    private StoYtLocationMapper stoYtLocationMapper;

    /**
     * 获取库位下拉框列表
     */
    public List<StoYtLocation> getLocationDropdown() {
        return stoYtLocationMapper.selectForDropdown();
    }

    /**
     * 根据库位名称获取库位ID，如果不存在则新增
     */
    public Long getLocationIdByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "库位名称不能为空");
        }

        // 尝试根据名称查找库位
        StoYtLocation query = new StoYtLocation();
        query.setName(name);
        List<StoYtLocation> locations = stoYtLocationMapper.selectList(query);

        if (locations != null && !locations.isEmpty()) {
            // 找到现有库位，返回ID
            return locations.get(0).getId();
        }

        // 库位不存在，创建新库位
        StoYtLocation newLocation = new StoYtLocation();
        newLocation.setName(name);
        newLocation.setIsDeleted(0);
        save(newLocation);

        return newLocation.getId();
    }

    /**
     * 保存库位信息，如果库位名称已存在则抛出异常
     */
    public void save(StoYtLocation location) {
        if (location == null || location.getName() == null || location.getName().trim().isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "库位信息不完整");
        }

        // 查询是否已存在同名库位
        StoYtLocation query = new StoYtLocation();
        query.setName(location.getName());
        List<StoYtLocation> existingLocations = stoYtLocationMapper.selectList(query);

        if (existingLocations != null && !existingLocations.isEmpty()) {
            // 检查是否是当前正在编辑的库位
            if (location.getId() == null || !existingLocations.get(0).getId().equals(location.getId())) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "库位名称已存在");
            }
        }

        if (location.getId() == null) {
            // 新增库位
            location.setIsDeleted(0);
            stoYtLocationMapper.insert(location);
        }
    }

    public StoYtLocation getById(Long id) {
        return stoYtLocationMapper.selectById(id);
    }

    public Map<Long, String> getLocationNameMap(Set<Long> set) {
        if (ValidityUtils.isEmpty(set)) {
            return Collections.emptyMap();
        }
        List<StoYtLocation> stoYtLocations = stoYtLocationMapper.selectBatchIds(set);
        return stoYtLocations.stream().collect(
                Collectors.toMap(StoYtLocation::getId, StoYtLocation::getName)
        );
    }
}
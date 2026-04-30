package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.material.dao.UmbrellaFrameMaterialDAO;
import com.qmy.zhongsheng.core.material.manager.UmbrellaFrameMaterialManager;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameMaterialDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class UmbrellaFrameMaterialManagerImpl implements UmbrellaFrameMaterialManager {

    private final UmbrellaFrameMaterialDAO umbrellaFrameMaterialDAO;

    @Override
    public Long saveOrUpdate(UmbrellaFrameMaterialDO row) {
        if (row.getId() == null) {
            umbrellaFrameMaterialDAO.insert(row);
            return row.getId();
        }
        umbrellaFrameMaterialDAO.updateById(row);
        return row.getId();
    }

    @Override
    public UmbrellaFrameMaterialDO getById(Long id) {
        return umbrellaFrameMaterialDAO.selectById(id);
    }

    @Override
    public List<UmbrellaFrameMaterialDO> listByUmbrellaFrameId(Long umbrellaFrameId) {
        return umbrellaFrameMaterialDAO.selectList(Wrappers.<UmbrellaFrameMaterialDO>lambdaQuery()
                .eq(UmbrellaFrameMaterialDO::getUmbrellaFrameId, umbrellaFrameId)
                .eq(UmbrellaFrameMaterialDO::getIsDeleted, 0)
                .orderByAsc(UmbrellaFrameMaterialDO::getId));
    }

    @Override
    public List<UmbrellaFrameMaterialDO> listByUmbrellaFrameIds(List<Long> umbrellaFrameIds) {
        if (umbrellaFrameIds == null || umbrellaFrameIds.isEmpty()) {
            return List.of();
        }
        return umbrellaFrameMaterialDAO.selectList(Wrappers.<UmbrellaFrameMaterialDO>lambdaQuery()
                .in(UmbrellaFrameMaterialDO::getUmbrellaFrameId, umbrellaFrameIds)
                .eq(UmbrellaFrameMaterialDO::getIsDeleted, 0)
                .orderByAsc(UmbrellaFrameMaterialDO::getId));
    }
}
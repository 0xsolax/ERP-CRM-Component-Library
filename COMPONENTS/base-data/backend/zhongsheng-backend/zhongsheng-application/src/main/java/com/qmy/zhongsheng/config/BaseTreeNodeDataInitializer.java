package com.qmy.zhongsheng.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.common.enums.BaseTreeNodeSeedEnum;
import com.qmy.zhongsheng.core.base.dao.BaseTreeNodeDAO;
import com.qmy.zhongsheng.core.base.model.entity.BaseTreeNodeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 启动时按 {@link BaseTreeNodeSeedEnum} 幂等补全 {@code base_tree_node}。
 *
 * @author AI Coding
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class BaseTreeNodeDataInitializer implements ApplicationRunner {

    private static final long ROOT_PARENT_ID = 0L;

    private final BaseTreeNodeDAO baseTreeNodeDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        Map<String, Long> seedKeyToId = new HashMap<>();
        int insertCount = 0;
        BaseTreeNodeSeedEnum[] baseTreeNodeSeedEnums = BaseTreeNodeSeedEnum.values();
        for (BaseTreeNodeSeedEnum def : baseTreeNodeSeedEnums) {
            Long parentId = def.getParentSeedKey() == null
                    ? ROOT_PARENT_ID
                    : Objects.requireNonNull(
                            seedKeyToId.get(def.getParentSeedKey()),
                            () -> "种子父节点未解析: parentSeedKey=" + def.getParentSeedKey() + ", seedKey=" + def.getNodeKey());
            BaseTreeNodeDO existing = baseTreeNodeDAO.selectOne(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                    .eq(BaseTreeNodeDO::getNodeKey, def.getNodeKey()));
            if (existing != null) {
                seedKeyToId.put(def.getNodeKey(), existing.getId());
                // 更新 nodeKey（如果之前没有设置）
                if (existing.getNodeKey() == null && def.getNodeKey() != null) {
                    BaseTreeNodeDO patch = new BaseTreeNodeDO();
                    patch.setId(existing.getId());
                    patch.setNodeKey(def.getNodeKey());
                    baseTreeNodeDAO.updateById(patch);
                }
                if (!Objects.equals(existing.getDataBindFlag(), def.getDataBindFlag())) {
                    BaseTreeNodeDO patch = new BaseTreeNodeDO();
                    patch.setId(existing.getId());
                    patch.setDataBindFlag(def.getDataBindFlag());
                    baseTreeNodeDAO.updateById(patch);
                }
                continue;
            }
            BaseTreeNodeDO row = new BaseTreeNodeDO();
            row.setBizType(def.getBizType().getValue());
            row.setParentId(parentId);
            row.setName(def.getName());
            row.setLevel(def.getLevel());
            row.setSortNum(def.getSortNum());
            row.setNodeKey(def.getNodeKey());
            row.setDataBindFlag(def.getDataBindFlag());
            baseTreeNodeDAO.insert(row);
            seedKeyToId.put(def.getNodeKey(), row.getId());
            insertCount++;
        }
        if (insertCount > 0) {
            log.info("base_tree_node 种子补全完成, insertCount={}", insertCount);
        }
    }
}
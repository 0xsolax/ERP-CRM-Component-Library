package com.qmy.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 基础树节点种子：启动时由 {@code BaseTreeNodeDataInitializer} 按 {@code bizType + parentId + name}
 * 幂等补全；父子顺序由枚举声明顺序保证，{@link #parentSeedKey} 指向父节点的 {@link #seedKey}。
 * <p>
 * 主键在插入时由 MyBatis-Plus 生成（{@code IdType.ASSIGN_ID}），不在此写死。
 * </p>
 *
 * @author AI Coding
 */
@Getter
@RequiredArgsConstructor
public enum BaseTreeNodeSeedEnum {

    /**
     * 尺寸管理示例
     */
    EXAMPLE_MGMT_SIZE(
            "EXAMPLE_MGMT_SIZE",
            BaseTreeBizTypeEnum.EXAMPLE_MGMT,
            null,
            "尺寸示例",
            1,
            1,
            0);

    /**
     * 种子内唯一键，用于解析父子关系（非库字段）。
     */
    private final String nodeKey;

    private final BaseTreeBizTypeEnum bizType;

    /**
     * 父节点枚举引用，根节点为 {@code null}。
     */
    private final BaseTreeNodeSeedEnum parentSeed;

    private final String name;

    private final Integer level;

    private final Integer sortNum;

    /**
     * 是否允许绑定基础数据：0 不允许，1 允许（须与 {@link BaseTreeBizTypeEnum#isLeafOnlyDataBind()} 规则一致）。
     */
    private final Integer dataBindFlag;

    /**
     * 获取父节点的 seedKey，用于数据库存储。
     *
     * @return 父节点的 seedKey，根节点返回 null
     */
    public String getParentSeedKey() {
        return parentSeed == null ? null : parentSeed.getNodeKey();
    }
}
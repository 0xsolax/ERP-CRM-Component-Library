package com.qmy.zhongsheng.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 基础树节点种子：启动时由 {@code BaseTreeNodeDataInitializer} 按 {@code bizType + parentId + name}
 * 幂等补全；父子顺序由枚举声明顺序保证，{@link #parentSeed} 指向父节点枚举。
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
     * 字段管理（一级）- 字段管理业务的根节点
     */
    FIELD_MGMT(
            "FIELD_MGMT",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            null,
            "字段管理",
            1,
            0,
            0),

    /**
     * 尺寸（二级）
     */
    FIELD_MGMT_SIZE(
            "FIELD_MGMT_SIZE",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT,
            "尺寸",
            2,
            1,
            0),

    FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH(
            "FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_SIZE,
            "伞架长度",
            3,
            1,
            1),

    FIELD_MGMT_SIZE_MIDDLE_POLE_DIAMETER(
            "FIELD_MGMT_SIZE_MIDDLE_POLE_DIAMETER",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_SIZE,
            "中棒直径",
            3,
            2,
            1),

    FIELD_MGMT_SIZE_RIB_COUNT(
            "FIELD_MGMT_SIZE_RIB_COUNT",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_SIZE,
            "伞骨数量",
            3,
            3,
            1),

    /**
     * 面料用量（二级）
     */
    FIELD_MGMT_FABRIC_USAGE(
            "FIELD_MGMT_FABRIC_USAGE",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT,
            "面料用量",
            2,
            2,
            1),

    /**
     * 伞架（二级）- 字段管理
     */
    FIELD_MGMT_UMBRELLA_FRAME(
            "FIELD_MGMT_UMBRELLA_FRAME",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT,
            "伞架",
            2,
            3,
            0),

    /**
     * 印刷（二级）
     */
    FIELD_MGMT_PRINTING(
            "FIELD_MGMT_PRINTING",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT,
            "印刷",
            2,
            4,
            0),

    FIELD_MGMT_UMBRELLA_FRAME_FUNCTION(
            "FIELD_MGMT_UMBRELLA_FRAME_FUNCTION",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_UMBRELLA_FRAME,
            "伞架功能",
            3,
            1,
            1),

    FIELD_MGMT_UMBRELLA_FRAME_TYPE(
            "FIELD_MGMT_UMBRELLA_FRAME_TYPE",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_UMBRELLA_FRAME,
            "伞架类型",
            3,
            2,
            1),

    FIELD_MGMT_UMBRELLA_FRAME_MATERIAL(
            "FIELD_MGMT_UMBRELLA_FRAME_MATERIAL",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_UMBRELLA_FRAME,
            "伞架材料",
            3,
            3,
            1),

    FIELD_MGMT_PRINTING_METHOD(
            "FIELD_MGMT_PRINTING_METHOD",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_PRINTING,
            "印刷方式",
            3,
            1,
            1),

    FIELD_MGMT_ALIGNMENT_METHOD(
            "FIELD_MGMT_ALIGNMENT_METHOD",
            BaseTreeBizTypeEnum.FIELD_MGMT,
            FIELD_MGMT_PRINTING,
            "对齐方式",
            3,
            2,
            1),

    /**
     * 面料（一级）- 材料管理
     */
    FABRIC(
            "FABRIC",
            BaseTreeBizTypeEnum.FABRIC,
            null,
            "面料",
            1,
            5,
            0),

    FABRIC_TYPE(
            "FABRIC_TYPE",
            BaseTreeBizTypeEnum.FABRIC,
            FABRIC,
            "种类",
            2,
            1,
            1),

    FABRIC_MODEL(
            "FABRIC_MODEL",
            BaseTreeBizTypeEnum.FABRIC,
            FABRIC,
            "型号",
            2,
            2,
            1),

    FABRIC_WIDTH(
            "FABRIC_WIDTH",
            BaseTreeBizTypeEnum.FABRIC,
            FABRIC,
            "门幅",
            2,
            3,
            1),

    /**
     * 包材（一级）- 材料管理
     */
    PACKAGING(
            "PACKAGING",
            BaseTreeBizTypeEnum.PACKAGING,
            null,
            "包材",
            1,
            6,
            0),

    PACKAGING_TYPE(
            "PACKAGING_TYPE",
            BaseTreeBizTypeEnum.PACKAGING,
            PACKAGING,
            "包材类型",
            2,
            1,
            1),

    /**
     * 产品（一级）- 产品管理
     */
    PRODUCT(
            "PRODUCT",
            BaseTreeBizTypeEnum.PRODUCT,
            null,
            "产品",
            1,
            7,
            0),

    PRODUCT_TYPE(
            "PRODUCT_TYPE",
            BaseTreeBizTypeEnum.PRODUCT,
            PRODUCT,
            "产品类型",
            2,
            1,
            1);

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
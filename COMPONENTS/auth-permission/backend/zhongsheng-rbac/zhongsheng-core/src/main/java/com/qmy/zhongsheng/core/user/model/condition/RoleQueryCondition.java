package com.qmy.zhongsheng.core.user.model.condition;

import com.qmy.zhongsheng.common.condition.PageQueryCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shanyitian
 * @description 构建manager查询入参
 * @date 2026/4/24 09:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleQueryCondition extends PageQueryCondition {

    private String likeName;

}

/*
 * @author java_deng
 * @date 2025/10/15 15:08
 * @description
 */
package com.qiaomoyun.interceptor;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.SkipTenantFilter;
import com.qiaomoyun.info.LoginUserInfo;
import com.qiaomoyun.util.LoginUserInfoContext;
import com.qiaomoyun.util.TenantInfoContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
public class TenantInterceptor implements Interceptor {

    private ArrayList<String> getPublicWhiteList(){
        ArrayList<String> whiteList = new ArrayList<>();
        whiteList.add("SysTenantMapper");// 租户表
        whiteList.add("SysTenantConfigMapper");// 租户配置表
        return whiteList;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("mybatis租户拦截器开启");
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement mappedStatement = (MappedStatement)
                metaObject.getValue("delegate.mappedStatement");

        log.debug("拦截的Mapper方法: {}", mappedStatement.getId());

        /**
         * 这里不能通过invocation.getMethod().getAnnotations()来获取注解，
         * 因为在mybatis拦截器中的invocation.getMethod()返回的是StatementHandler的prepare方法，
         * 是 MyBatis 生成的代理方法，而不是被注解的原方法，所以会获取不到注解
         */


        // 获取Mapper接口方法
        String mappedStatementId = mappedStatement.getId();
        String mapperClassName = mappedStatementId.substring(0, mappedStatementId.lastIndexOf('.'));
        String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf('.') + 1);

        try {
            // 反射获取Mapper接口
            Class<?> mapperClass = Class.forName(mapperClassName);
            if(getPublicWhiteList().contains(mapperClassName.substring(mapperClassName.lastIndexOf('.') + 1))){
                //租户相关方法跳过租户拦截
                return invocation.proceed(); // 跳过处理
            }
            // 查找方法并检查是否有SkipTenantFilter注解
            for (Method m : mapperClass.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    if (m.isAnnotationPresent(SkipTenantFilter.class)) {
                        return invocation.proceed(); // 跳过处理
                    }
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            log.warn("无法加载Mapper类: {}", mapperClassName);
        }

        Integer tenantId = TenantInfoContext.getCurrentTenantId();
        if (tenantId == null) {
            log.warn("未找到租户ID，跳过拦截");
            throw new BizException(ExceptionCodeEnum.Tenant_Exception.getCode(), ExceptionCodeEnum.Tenant_Exception.getValue());
        }

        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();
        String modifiedSql = addTenantCondition(originalSql, tenantId);

        metaObject.setValue("delegate.boundSql.sql", modifiedSql);
        return invocation.proceed();
    }


    private String addTenantCondition(String sql, Integer tenantId) throws JSQLParserException {
//        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select) {
                return processSelect((Select) statement, tenantId);
            } else if (statement instanceof Update) {
                return processUpdate((Update) statement, tenantId);
            } else if (statement instanceof Delete) {
                return processDelete((Delete) statement, tenantId);
            }

//        } catch (JSQLParserException e) {
//            log.warn("SQL解析添加租户筛选失败");
//            throw new BizException(ExceptionCodeEnum.Tenant_Exception.getCode(), "SQL解析添加租户筛选失败");
////            return addTenantConditionWithRegex(sql, tenantId);
//        }
        return sql;
    }

    private String processSelect(Select select, Integer tenantId) throws JSQLParserException {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();

        // 获取主表信息（通常是FROM子句中的第一个表）
        String tableName = null;
        if (plainSelect.getFromItem() != null) {
            // 处理FROM子句是嵌套查询的情况
            if (plainSelect.getFromItem() instanceof net.sf.jsqlparser.statement.select.SubSelect) {
                // 递归处理子查询
                net.sf.jsqlparser.statement.select.SubSelect subSelect = (net.sf.jsqlparser.statement.select.SubSelect) plainSelect.getFromItem();
                // 处理子查询
                String subSelectSql = processSelect((Select) CCJSqlParserUtil.parse(subSelect.getSelectBody().toString()), tenantId);
                // 更新子查询
                subSelect.setSelectBody(((Select) CCJSqlParserUtil.parse(subSelectSql)).getSelectBody());
                // 获取表别名
                tableName = subSelect.getAlias() != null ? subSelect.getAlias().getName() : null;
            } else {
                // 处理pageHepler中，将整条SQL语句作为表计算数量的处理
                if (plainSelect.getFromItem() instanceof net.sf.jsqlparser.statement.select.SubSelect) {
                    net.sf.jsqlparser.statement.select.SubSelect subSelect = (net.sf.jsqlparser.statement.select.SubSelect) plainSelect.getFromItem();
                    // 检查是否是pageHelper生成的count查询（别名包含table_count）
                    if (subSelect.getAlias() != null && subSelect.getAlias().getName().equals("table_count")) {
                        // 直接处理子查询
                        String subSelectSql = processSelect((Select) CCJSqlParserUtil.parse(subSelect.getSelectBody().toString()), tenantId);
                        // 更新子查询
                        subSelect.setSelectBody(((Select) CCJSqlParserUtil.parse(subSelectSql)).getSelectBody());
                        // 直接返回处理后的查询
                        return select.toString();
                    }
                }

                // 处理普通表查询
                tableName = plainSelect.getFromItem().toString();
                // 处理有别名的情况，如table t
                if (tableName.contains(" ")) {
                    if(tableName.contains(" as ") || tableName.contains(" AS ")){
                        tableName = tableName.split(" ")[2];
                    }else {
                        tableName = tableName.split(" ")[1];
                    }
                } else {
                    // 没有别名的情况，直接使用表名
                    tableName = plainSelect.getFromItem().toString();
                }
            }
        }

        // 创建租户过滤条件，带表名/别名前缀, 在租户筛选条件前加上主表名称或别名
        // 注意：如果是嵌套查询，tableName是别名，不需要添加tenant_id条件到主查询
        // 租户条件应该已经在子查询中添加
        if (tableName != null && !(plainSelect.getFromItem() instanceof net.sf.jsqlparser.statement.select.SubSelect)) {
            Expression tenantCondition = new EqualsTo()
                    .withLeftExpression(new Column(tableName + ".tenant_id"))
                    .withRightExpression(new LongValue(tenantId));

            if (where == null) {
                plainSelect.setWhere(tenantCondition);
            } else {
                plainSelect.setWhere(new AndExpression(where, tenantCondition));
            }
        }
        // 实现数据权限：根据filterFields添加筛选条件
        LoginUserInfo loginUserInfo = LoginUserInfoContext.getLoginUserInfo();
        if (loginUserInfo != null) {
            Boolean isOrganizeData = loginUserInfo.getIsOrganizeData();
            if (isOrganizeData != null && isOrganizeData) {
                // 从LoginUserInfo中获取已经在web拦截器中准备好的createUserIdList
                List<Long> createUserIdList = loginUserInfo.getCreateUserIdList();
                // 获取条件信息
                List<LoginUserInfo.ConditionInfo> conditions = loginUserInfo.getConditions();
                if (conditions == null || conditions.isEmpty()) {
                    // 默认条件
                    conditions = new ArrayList<>();
                    conditions.add(new LoginUserInfo.ConditionInfo("create_user", "AND"));
                }

                // 添加筛选条件进行数据权限过滤
                if (createUserIdList != null && createUserIdList.size() > 0 && tableName != null) {
                    Expression currentWhere = plainSelect.getWhere();
                    Expression newWhere = null;

                    // 为每个条件添加IN条件
                    for (int i = 0; i < conditions.size(); i++) {
                        LoginUserInfo.ConditionInfo condition = conditions.get(i);
                        String field = condition.getField();
                        String logic = condition.getLogic();

                        // 创建IN表达式
                        InExpression fieldCondition = new InExpression();
                        fieldCondition.setLeftExpression(new Column(tableName + "." + field));

                        // 设置IN的右边值列表
                        List<Expression> expressions = new ArrayList<>();
                        for (Long userId : createUserIdList) {
                            expressions.add(new LongValue(userId));
                        }
                        fieldCondition.setRightItemsList(new net.sf.jsqlparser.expression.operators.relational.ItemsList() {
                            @Override
                            public void accept(net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor itemsListVisitor) {
                                // 实现必要的方法
                            }

                            @Override
                            public String toString() {
                                StringBuilder sb = new StringBuilder("(");
                                for (int j = 0; j < expressions.size(); j++) {
                                    sb.append(expressions.get(j));
                                    if (j < expressions.size() - 1) {
                                        sb.append(", ");
                                    }
                                }
                                sb.append(")");
                                return sb.toString();
                            }
                        });

                        // 组合多个筛选条件
                        if (newWhere == null) {
                            newWhere = fieldCondition;
                        } else {
                            if ("OR".equals(logic)) {
                                newWhere = new OrExpression(newWhere, fieldCondition);
                            } else {
                                newWhere = new AndExpression(newWhere, fieldCondition);
                            }
                        }
                    }

                    // 将筛选条件添加到WHERE子句
                    if (newWhere != null) {
                        // 创建括号表达式，将所有数据权限条件括起来
                        net.sf.jsqlparser.expression.Parenthesis parenthesis = new net.sf.jsqlparser.expression.Parenthesis(newWhere);

                        if (currentWhere == null) {
                            plainSelect.setWhere(parenthesis);
                        } else {
                            plainSelect.setWhere(new AndExpression(currentWhere, parenthesis));
                        }
                        // 构建日志信息
                        StringBuilder logMsg = new StringBuilder();
                        for (int i = 0; i < conditions.size(); i++) {
                            if (i > 0) {
                                logMsg.append(" " + conditions.get(i).getLogic() + " ");
                            }
                            logMsg.append(conditions.get(i).getField());
                        }
                        log.info("已添加数据权限过滤: {} in {}", logMsg.toString(), createUserIdList);
                    }
                }
            }
        }

        return select.toString();
    }

    private String processUpdate(Update update, Integer tenantId) {
        Expression where = update.getWhere();
        // 获取更新的表名
        String tableName = update.getTable().getName();
        // 创建租户过滤条件，带表名前缀
        Expression tenantCondition = new EqualsTo()
                .withLeftExpression(new Column(tableName + ".tenant_id"))
                .withRightExpression(new LongValue(tenantId));

        if (where == null) {
            update.setWhere(tenantCondition);
        } else {
            update.setWhere(new AndExpression(where, tenantCondition));
        }
        return update.toString();
    }

    private String processDelete(Delete delete, Integer tenantId) {
        Expression where = delete.getWhere();
        // 获取删除的表名
        String tableName = delete.getTable().getName();
        // 创建租户过滤条件，带表名前缀
        Expression tenantCondition = new EqualsTo()
                .withLeftExpression(new Column(tableName + ".tenant_id"))
                .withRightExpression(new LongValue(tenantId));

        if (where == null) {
            delete.setWhere(tenantCondition);
        } else {
            delete.setWhere(new AndExpression(where, tenantCondition));
        }
        return delete.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}

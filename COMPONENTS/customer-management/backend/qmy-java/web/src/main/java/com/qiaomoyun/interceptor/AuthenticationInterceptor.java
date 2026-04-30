package com.qiaomoyun.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequiresDataPermissions;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.entity.sys.SysDepartmentLeader;
import com.qiaomoyun.entity.sys.SysDepartmentUser;
import com.qiaomoyun.entity.sys.SysTenantConfig;
import com.qiaomoyun.entity.sys.SysUserDataPermission;
import com.qiaomoyun.manager.sys.SysPermissionManager;
import com.qiaomoyun.manager.sys.SysRoleManager;
import com.qiaomoyun.mapper.sys.SysDepartmentLeaderMapper;
import com.qiaomoyun.mapper.sys.SysDepartmentUserMapper;
import com.qiaomoyun.mapper.sys.SysTenantConfigMapper;
import com.qiaomoyun.mapper.sys.SysUserDataPermissionMapper;
import com.qiaomoyun.mapper.sys.SysUserMapper;
import com.qiaomoyun.service.SysUserService;
import com.qiaomoyun.entity.sys.SysUser;
import com.qiaomoyun.info.LoginUserInfo;
import com.qiaomoyun.util.LoginUserInfoContext;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.vo.sys.SysUserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Moto
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysRoleManager sysRoleManager;
    @Resource
    private SysPermissionManager sysPermissionManager;
    @Resource
    private SysTenantConfigMapper sysTenantConfigMapper;
    @Resource
    private SysUserDataPermissionMapper sysUserDataPermissionMapper;
    @Resource
    private SysDepartmentLeaderMapper sysDepartmentLeaderMapper;
    @Resource
    private SysDepartmentUserMapper sysDepartmentUserMapper;
    @Resource
    private SysUserMapper sysUserMapper;



    private static final String token_header="qiaomoyun-token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从请求头中获取token
        String token = request.getHeader(token_header);

        // 这里进行登录token校验逻辑
        if (token == null || !sysUserService.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(ResultInfo.error(401, "未登录或登录已过期")));
            return false;
        }

        //获取用户id
        Long userId = sysUserService.getUserIdByToken(token);

        // 获取真实IP（考虑代理情况）
        String ip = getClientIp(request);

        // 获取User-Agent
        String ua = request.getHeader("User-Agent");

        // 记录登录用户信息，包括IP和UA
        LoginUserInfo loginUserInfo = new LoginUserInfo(
                userId,
                token,
                ip,
                ua
        );
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser != null) {
            loginUserInfo.setNickName(sysUser.getNickName());
        }
        //校验是否有请求接口的权限
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RequiresPermissions methodAnnotation = method.getAnnotation(RequiresPermissions.class);
        if (methodAnnotation != null) {
            //获取到了权限注解，说明这个接口开启了权限需要校验
            //获取权限字符串
            String per = Arrays.toString(methodAnnotation.value());
            per=per.replace("[","").replace("]","");

            if(StringUtils.isNotBlank(per)){
                /**
                 * 说明需要校验权限，拥有该权限字符串，才能访问该方法
                 * 此时，需要从request头信息中拿到token，然后解密token，得到用户的权限集合，然后再做判断即可
                 */
                SysUserVO user = sysUserService.detail(userId);
                List<Integer> roleIds = user.getRoleIds();
                if(ObjectUtil.isEmpty(roleIds)){
                    response.getWriter().write(JSON.toJSONString(ResultInfo.error(ExceptionCodeEnum.Login_Exception.getCode(), "该账号未分配角色或角色已被禁用")));
                    return false;
                }
                Set<String> permissions =new HashSet<>(sysPermissionManager.getPermissionsByRoleIds(roleIds));
                //开始判断
                if(!permissions.contains(per)&&!permissions.contains("*")){
                    response.getWriter().write(JSON.toJSONString(ResultInfo.error(ExceptionCodeEnum.Login_Exception.getCode(), "暂无权限，请联系管理员")));
                    return false;
                }

                //校验数据权限，查找这个方法是否被RequiresDataPermissions注解
                RequiresDataPermissions requiresDataPermissions = method.getAnnotation(RequiresDataPermissions.class);
                if(requiresDataPermissions!=null){
                    String tenantId = request.getHeader(TenantWebInterceptor.Tenant_Header);
                    /**
                     * 校验这个租户的这个方法是否开启了数据权限
                     */
                    SysTenantConfig config = sysTenantConfigMapper.getDataPermissionByTenantIdAndConfigValue(tenantId, per);
                    if(!ObjectUtil.isEmpty(config)){
                        //如果开启了数据权限，则需要去数据库查询这个用户是否是全部数据，如果不是，则需要在TheaderLocal中做好标识，在mybatis拦截器中去拦截
                        SysUserDataPermission dataPermission = sysUserDataPermissionMapper.getByUserIdAndPermission(userId, config.getConfigValue());
                        if(ObjectUtil.isEmpty(dataPermission)||dataPermission.getIsOrganizeData()==2){
                            //在TheaderLocal中做好标识，在mybatis拦截器中去拦截
                            loginUserInfo.setIsOrganizeData(true);

                            // 获取数据权限的createUserIdList
                            List<Long> createUserIdList = new ArrayList<>();
                            createUserIdList.add(userId);

                            // 查找负责的部门
                            List<SysDepartmentLeader> sysDepartmentLeaderList = sysDepartmentLeaderMapper.getByUserId(userId, Integer.valueOf(tenantId));
                            if(!ObjectUtil.isEmpty(sysDepartmentLeaderList)){
                                for(SysDepartmentLeader sysDepartmentLeader : sysDepartmentLeaderList){
                                    Long departmentId = sysDepartmentLeader.getDepartmentId();
                                    List<SysDepartmentUser> departmentUsers = sysDepartmentUserMapper.getByDepartmentId(departmentId, Integer.valueOf(tenantId));
                                    if(!ObjectUtil.isEmpty(departmentUsers)){
                                        departmentUsers.forEach(sysDepartmentUser -> {
                                            createUserIdList.add(sysDepartmentUser.getUserId());
                                        });
                                    }
                                }
                            }

                            // 设置到LoginUserInfo中
                            loginUserInfo.setCreateUserIdList(createUserIdList);
                            // 设置条件信息
                            List<LoginUserInfo.ConditionInfo> conditionInfos = new ArrayList<>();
                            for (RequiresDataPermissions.Condition condition : requiresDataPermissions.conditions()) {
                                conditionInfos.add(new LoginUserInfo.ConditionInfo(condition.field(), condition.logic().name()));
                            }
                            loginUserInfo.setConditions(conditionInfos);
                        }
                    }
                }
            }
        }
        // 记录登录用户信息
        LoginUserInfoContext.setLoginUserInfo(loginUserInfo);
        return true;
    }

    /**
     * 获取客户端真实IP（考虑代理服务器）
     */
    private String getClientIp(HttpServletRequest request) {
        String xffHeader = request.getHeader("X-Forwarded-For");
        if (xffHeader == null) {
            return request.getRemoteAddr();
        }
        // 取X-Forwarded-For中第一个非unknown的有效IP字符串
        String[] ips = xffHeader.split(",");
        for (String ip : ips) {
            if (!"unknown".equalsIgnoreCase(ip)) {
                return ip.trim();
            }
        }
        return request.getRemoteAddr();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserInfoContext.clear();
    }

}

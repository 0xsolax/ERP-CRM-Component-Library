package com.qmy.project.aspect;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 对标注了 {@link org.springframework.web.bind.annotation.GetMapping} 或
 * {@link org.springframework.web.bind.annotation.PostMapping} 的方法记录请求与响应日志。
 *
 * @author AI Coding
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        StringBuilder requestLogBuilder = new StringBuilder();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestLogBuilder.append("请求URL: ").append(request.getRequestURL()).append("\n")
                    .append("请求方法: ").append(request.getMethod()).append("\n")
                    .append("请求IP: ").append(request.getRemoteAddr()).append("\n")
                    .append("请求类方法: ").append(joinPoint.getSignature()).append("\n")
                    .append("请求参数: ").append(Arrays.toString(joinPoint.getArgs()));
        }

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            StringBuilder logBuilder = new StringBuilder(requestLogBuilder);

            if (exception != null) {
                logBuilder.append("\n方法执行异常: ").append(exception.getClass().getName()).append("\n")
                        .append("异常信息: ").append(exception.getMessage()).append("\n")
                        .append("方法执行时间: ").append(endTime - startTime).append(" ms");
                log.info(logBuilder.toString());
            } else {
                logBuilder.append("\n方法执行时间: ").append(endTime - startTime).append(" ms").append("\n")
                        .append("响应结果: ").append(JSONUtil.toJsonStr(result));
                log.info(logBuilder.toString());
            }
        }

        return result;
    }
}

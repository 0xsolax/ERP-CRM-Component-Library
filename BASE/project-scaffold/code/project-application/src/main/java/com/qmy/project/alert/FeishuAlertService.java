package com.qmy.project.alert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.core.tenant.service.TenantConfigService;
import com.qmy.project.support.TraceIdSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuAlertService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    private final ObjectMapper objectMapper;

    private final TenantConfigService tenantConfigService;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public void sendServerErrorAlert(Exception exception) {
        if (isLocalActiveProfile()) {
            return;
        }
        List<TenantConfigDO> feishuRows = tenantConfigService.listTenantConfigsForFeishuAlert();
        Map<String, String> cfg = feishuRows.stream()
                .filter(r -> r.getConfigCode() != null)
                .collect(Collectors.toMap(TenantConfigDO::getConfigCode, TenantConfigDO::getConfigValue, (a, b) -> b));
        String webhookUrl = cfg.get(TenantConfigCodeConstants.FEISHU_WEBHOOK_URL);
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        String traceId = MDC.get(TraceIdSupport.TRACE_ID_KEY);
        HttpServletRequest request = currentRequest();
        FeishuAlertMessageBuilder.AlertMessage alertMessage =
                FeishuAlertMessageBuilder.build(applicationName, traceId, exception, request, formatTriggerUserLine());

        try {
            String requestBody = objectMapper.writeValueAsString(buildPayload(alertMessage));
            HttpRequest requestMessage = HttpRequest.newBuilder(URI.create(webhookUrl))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .timeout(REQUEST_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(requestMessage, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("[traceId={}] 飞书告警发送失败, status={}, body={}",
                        traceId, response.statusCode(), truncate(response.body()));
                return;
            }

            if (!isFeishuSuccess(response.body())) {
                log.warn("[traceId={}] 飞书告警发送结果异常, body={}", traceId, truncate(response.body()));
                return;
            }

            log.info("[traceId={}] 飞书告警发送成功", traceId);
        } catch (Exception sendException) {
            log.error("[traceId={}] 飞书告警发送异常", traceId, sendException);
        }
    }

    private boolean isLocalActiveProfile() {
        if (activeProfile == null || activeProfile.isBlank()) {
            return false;
        }
        for (String segment : activeProfile.split(",")) {
            if ("local".equalsIgnoreCase(segment.trim())) {
                return true;
            }
        }
        return false;
    }

    private static String formatTriggerUserLine() {
        LoginUserInfo info = LoginUserInfoContext.getLoginUserInfo();
        if (info == null || info.getUserId() == null) {
            return "-";
        }
        String display = info.getNickname();
        if (display == null || display.isBlank()) {
            display = info.getUserName();
        }
        if (display == null || display.isBlank()) {
            display = "-";
        }
        return info.getUserId() + "(" + display + ")";
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private Map<String, Object> buildPayload(FeishuAlertMessageBuilder.AlertMessage alertMessage) {
        Map<String, Object> zhCn = new LinkedHashMap<>();
        zhCn.put("title", alertMessage.getTitle());
        zhCn.put("content", buildPostLines(alertMessage.getLines()));

        Map<String, Object> post = new LinkedHashMap<>();
        post.put("zh_cn", zhCn);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("post", post);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("msg_type", "post");
        payload.put("content", content);
        return payload;
    }

    private List<List<Map<String, String>>> buildPostLines(List<String> lines) {
        List<List<Map<String, String>>> content = new ArrayList<>();
        for (String line : lines) {
            Map<String, String> paragraph = new LinkedHashMap<>();
            paragraph.put("tag", "text");
            paragraph.put("text", line);
            content.add(List.of(paragraph));
        }
        return content;
    }

    private boolean isFeishuSuccess(String responseBody) {
        try {
            JsonNode responseJson = objectMapper.readTree(responseBody);
            return responseJson.path("code").asInt(-1) == 0;
        } catch (Exception parseException) {
            log.warn("飞书告警返回内容解析失败, body={}", truncate(responseBody), parseException);
            return false;
        }
    }

    private String truncate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        if (value.length() <= 500) {
            return value;
        }
        return value.substring(0, 497) + "...";
    }
}

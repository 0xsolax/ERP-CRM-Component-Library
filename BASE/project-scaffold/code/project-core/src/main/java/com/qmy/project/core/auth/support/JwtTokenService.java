package com.qmy.project.core.auth.support;

import cn.hutool.json.JSONUtil;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.core.auth.model.JwtTokenClaims;
import com.qmy.project.core.auth.model.AuthSettings;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT 签发与校验（依赖 {@link AuthSettings} 中的密钥与过期时间）。
 */
@Component
@RequiredArgsConstructor
public class JwtTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final AuthSettings authSettings;

    public String createAccessToken(Long userId, String tokenId, String loginType, String platform) {
        long now = Instant.now().getEpochSecond();
        long expireAt = now + authSettings.getJwt().getAccessTokenExpireSeconds();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", authSettings.getJwt().getIssuer());
        payload.put("sub", String.valueOf(userId));
        payload.put("uid", userId);
        payload.put("jti", tokenId);
        payload.put("loginType", loginType);
        payload.put("platform", platform);
        payload.put("iat", now);
        payload.put("exp", expireAt);

        String encodedHeader = base64UrlEncode(JSONUtil.toJsonStr(header));
        String encodedPayload = base64UrlEncode(JSONUtil.toJsonStr(payload));
        String signature = sign(encodedHeader + "." + encodedPayload);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public JwtTokenClaims parseAndValidate(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_INVALID);
        }

        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_INVALID);
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = JSONUtil.toBean(payloadJson, Map.class);

        long expireAt = longValue(payload.get("exp"));
        long now = Instant.now().getEpochSecond();
        if (expireAt <= now) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_EXPIRED);
        }

        JwtTokenClaims claims = new JwtTokenClaims();
        claims.setUserId(longValue(payload.get("uid")));
        claims.setTokenId(stringValue(payload.get("jti")));
        claims.setLoginType(stringValue(payload.get("loginType")));
        claims.setPlatform(stringValue(payload.get("platform")));
        claims.setIssuedAt(longValue(payload.get("iat")));
        claims.setExpiresAt(expireAt);
        return claims;
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(authSettings.getJwt().getSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] signed = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signed);
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_INVALID.getCode(), "JWT 签名失败");
        }
    }

    private String base64UrlEncode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private Long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}

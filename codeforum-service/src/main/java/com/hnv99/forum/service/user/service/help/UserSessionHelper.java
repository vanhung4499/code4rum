package com.hnv99.forum.service.user.service.help;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.mdc.SelfTraceIdGenerator;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.MapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Using JWT to store user tokens, there's no need for the backend to store sessions anymore
 */
@Slf4j
@Component
public class UserSessionHelper {
    @Component
    @Data
    @ConfigurationProperties("codeforum.jwt")
    public static class JwtProperties {
        /**
         * Issuer
         */
        private String issuer;
        /**
         * Secret key
         */
        private String secret;
        /**
         * Expiration time, in milliseconds
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    public UserSessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    public String genSession(Long userId) {
        // 1. Generate a JWT-formatted session, containing expiration time and user information
        String session = JsonUtil.toStr(MapUtils.create("s", SelfTraceIdGenerator.generate(), "u", userId));
        String token = JWT.create().withIssuer(jwtProperties.getIssuer()).withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);

        // 2. When using the token generated by JWT, the backend can omit storing this session information and rely entirely on JWT information
        // However, consider user logout, where the token needs to be invalidated actively. Since JWT itself is stateless, here Redis is used to cache a simple token -> userId mapping for double verification
        RedisClient.setStrWithExpire(token, String.valueOf(userId), jwtProperties.getExpire() / 1000);
        return token;
    }

    public void removeSession(String session) {
        RedisClient.del(session);
    }

    /**
     * Get user information based on session
     *
     * @param session
     * @return
     */
    public Long getUserIdBySession(String session) {
        // JWT verification method, if the token is illegal or expired, the signature verification fails directly
        try {
            DecodedJWT decodedJWT = verifier.verify(session);
            String pay = new String(Base64Utils.decodeFromString(decodedJWT.getPayload()));
            // If JWT verification passes, get the corresponding userId
            String userId = String.valueOf(JsonUtil.toObj(pay, HashMap.class).get("u"));

            // Retrieve userId from Redis to address the issue of user logout and backend token invalidation
            String user = RedisClient.getStr(session);
            if (user == null || !Objects.equals(userId, user)) {
                return null;
            }
            return Long.valueOf(user);
        } catch (Exception e) {
            log.info("JWT token verification failed! Token: {}, Message: {}", session, e.getMessage());
            return null;
        }
    }
}


package com.hnv99.forum.api.model.context;

import com.hnv99.forum.api.model.vo.seo.Seo;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import lombok.Data;

import java.security.Principal;

/**
 * Request context, carrying user identity-related information
 */
public class ReqInfoContext {
    private static ThreadLocal<ReqInfo> contexts = new InheritableThreadLocal<>();

    public static void addReqInfo(ReqInfo reqInfo) {
        contexts.set(reqInfo);
    }

    public static void clear() {
        contexts.remove();
    }

    public static ReqInfo getReqInfo() {
        return contexts.get();
    }

    @Data
    public static class ReqInfo implements Principal {
        /**
         * appKey
         */
        private String appKey;
        /**
         * Host accessed
         */
        private String host;
        /**
         * Accessed path
         */
        private String path;
        /**
         * Client IP
         */
        private String clientIp;
        /**
         * Referer
         */
        private String referer;
        /**
         * Post form parameters
         */
        private String payload;
        /**
         * Device information
         */
        private String userAgent;

        /**
         * Login session
         */
        private String session;

        /**
         * User ID
         */
        private Long userId;
        /**
         * User information
         */
        private BaseUserInfoDTO user;
        /**
         * Message count
         */
        private Integer msgNum;

        private Seo seo;

        private String deviceId;

        @Override
        public String getName() {
            return session;
        }
    }
}

package com.hnv99.forum.core.util;

import org.springframework.util.Assert;

/**
 * Utility class for environment-related operations
 */
public class EnvUtil {
    private static volatile EnvEnum env;

    public enum EnvEnum {
        DEV("dev", false),
        TEST("test", false),
        PRE("pre", false),
        PROD("prod", true);
        private String env;
        private boolean prod;

        EnvEnum(String env, boolean prod) {
            this.env = env;
            this.prod = prod;
        }

        public static EnvEnum nameOf(String name) {
            for (EnvEnum env : values()) {
                if (env.env.equalsIgnoreCase(name)) {
                    return env;
                }
            }
            return null;
        }
    }

    /**
     * Checks if the environment is production
     *
     * @return true if the environment is production, false otherwise
     */
    public static boolean isPro() {
        return getEnv().prod;
    }

    /**
     * Retrieves the current environment
     *
     * @return the current environment
     */
    public static EnvEnum getEnv() {
        if (env == null) {
            synchronized (EnvUtil.class) {
                if (env == null) {
                    env = EnvEnum.nameOf(SpringUtil.getConfig("env.name"));
                }
            }
        }
        Assert.isTrue(env != null, "Environment configuration 'env.name' must exist!");
        return env;
    }
}

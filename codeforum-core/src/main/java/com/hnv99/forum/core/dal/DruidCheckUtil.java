package com.hnv99.forum.core.dal;

import com.github.hui.quick.plugin.qrcode.util.ClassUtils;

/**
 * Utility class for checking the presence of Druid-related packages.
 *
 * author YiHui
 * date 2023/5/28
 */
public class DruidCheckUtil {

    /**
     * Check whether Druid-related packages are present.
     *
     * @return true if the packages are present, false otherwise
     */
    public static boolean hasDuridPkg() {
        return ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", DataSourceConfig.class.getClassLoader());
    }

}

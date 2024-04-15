package com.hnv99.forum.core.net;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hnv99.forum.core.config.ProxyProperties;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Proxy Center
 */
public class ProxyCenter {

    /**
     * Record the index of proxy used by each source
     */
    private static final Cache<String, Integer> HOST_PROXY_INDEX = Caffeine.newBuilder().maximumSize(16).build();

    /**
     * Proxy list
     */
    private static List<ProxyProperties.ProxyType> PROXIES = new ArrayList<>();

    /**
     * Initialize the proxy pool
     *
     * @param proxyTypes List of proxy types
     */
    public static void initProxyPool(List<ProxyProperties.ProxyType> proxyTypes) {
        PROXIES = proxyTypes;
    }

    /**
     * Get proxy
     *
     * @param host Host name
     * @return ProxyType
     */
    static ProxyProperties.ProxyType getProxy(String host) {
        Integer index = HOST_PROXY_INDEX.getIfPresent(host);
        if (index == null) {
            index = -1;
        }

        ++index;
        if (index >= PROXIES.size()) {
            index = 0;
        }
        HOST_PROXY_INDEX.put(host, index);
        return PROXIES.get(index);
    }

    /**
     * Load proxy
     *
     * @param host Host name
     * @return Proxy
     */
    public static Proxy loadProxy(String host) {
        ProxyProperties.ProxyType proxyType = getProxy(host);
        if (proxyType == null) {
            return null;
        }
        return new Proxy(proxyType.getType(), new InetSocketAddress(proxyType.getIp(), proxyType.getPort()));
    }
}

package com.hnv99.forum.core.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: This class can be deprecated after adding the RabbitMQ connection pool.
 */
public class RabbitmqUtil {

    /**
     * Each host has its own factory for later transformation into a multi-machine mode.
     */
    private static Map<String, ConnectionFactory> executors = new ConcurrentHashMap<>();

    /**
     * Initialize a factory.
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @param virtualHost
     * @return
     */
    private static ConnectionFactory init(String host,
                                          Integer port,
                                          String username,
                                          String password,
                                          String virtualHost) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        return factory;
    }

    /**
     * Factory singleton, each host has its own factory.
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @param virtualHost
     * @return
     */
    public static ConnectionFactory getOrInitConnectionFactory(String host,
                                                               Integer port,
                                                               String username,
                                                               String password,
                                                               String virtualHost) {
        String key = getConnectionFactoryKey(host, port);
        ConnectionFactory connectionFactory = executors.get(key);
        if (null == connectionFactory) {
            synchronized (RabbitmqUtil.class) {
                connectionFactory = executors.get(key);
                if (null == connectionFactory) {
                    connectionFactory = init(host, port, username, password, virtualHost);
                    executors.put(key, connectionFactory);
                }
            }
        }
        return connectionFactory;
    }

    /**
     * Get the key.
     * @param host
     * @param port
     * @return
     */
    private static String getConnectionFactoryKey(String host, Integer port) {
        return host + ":" + port;
    }
}

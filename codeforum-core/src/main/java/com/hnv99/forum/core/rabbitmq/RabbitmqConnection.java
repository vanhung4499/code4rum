package com.hnv99.forum.core.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitmqConnection class for establishing and managing RabbitMQ connections.
 */
public class RabbitmqConnection {

    private Connection connection;

    /**
     * Constructor to initialize the RabbitMQ connection.
     *
     * @param host        The RabbitMQ server host.
     * @param port        The RabbitMQ server port.
     * @param userName    The username for authentication.
     * @param password    The password for authentication.
     * @param virtualhost The virtual host for the connection.
     */
    public RabbitmqConnection(String host, int port, String userName, String password, String virtualhost) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualhost);
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the established connection.
     *
     * @return The RabbitMQ connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Close the RabbitMQ connection.
     */
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


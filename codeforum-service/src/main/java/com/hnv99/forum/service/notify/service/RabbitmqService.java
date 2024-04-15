package com.hnv99.forum.service.notify.service;


import com.rabbitmq.client.BuiltinExchangeType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Service interface for RabbitMQ operations.
 */
public interface RabbitmqService {

    /**
     * Check if RabbitMQ is enabled.
     *
     * @return true if RabbitMQ is enabled, false otherwise
     */
    boolean enabled();

    /**
     * Publish a message to RabbitMQ.
     *
     * @param exchange     the exchange to publish the message to
     * @param exchangeType the type of the exchange
     * @param routingKey   the routing key
     * @param message      the message to be published
     * @throws IOException     if an I/O error occurs
     * @throws TimeoutException if the operation times out
     */
    void publishMsg(String exchange,
                    BuiltinExchangeType exchangeType,
                    String routingKey,
                    String message) throws IOException, TimeoutException;

    /**
     * Consume messages from RabbitMQ.
     *
     * @param exchange   the exchange to consume messages from
     * @param queue      the queue to consume messages from
     * @param routingKey the routing key
     * @throws IOException     if an I/O error occurs
     * @throws TimeoutException if the operation times out
     */
    void consumerMsg(String exchange,
                     String queue,
                     String routingKey) throws IOException, TimeoutException;

    /**
     * Process consumer messages.
     */
    void processConsumerMsg();
}

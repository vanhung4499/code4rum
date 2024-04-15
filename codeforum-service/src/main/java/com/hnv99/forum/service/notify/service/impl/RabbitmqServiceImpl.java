package com.hnv99.forum.service.notify.service.impl;

import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.core.common.CommonConstants;
import com.hnv99.forum.core.rabbitmq.RabbitmqConnection;
import com.hnv99.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.notify.service.NotifyService;
import com.hnv99.forum.service.notify.service.RabbitmqService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    private NotifyService notifyService;

    @Override
    public boolean enabled() {
        return "true".equalsIgnoreCase(SpringUtil.getConfig("rabbitmq.switchFlag"));
    }

    @Override
    public void publishMsg(String exchange,
                           BuiltinExchangeType exchangeType,
                           String routingKey,
                           String message) {
        try {
            // Create connection
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            // Create channel
            Channel channel = connection.createChannel();
            // Declare exchange with message persistence and no auto-delete
            channel.exchangeDeclare(exchange, exchangeType, true, false, null);
            // Publish message
            channel.basicPublish(exchange, routingKey, null, message.getBytes());
            log.info("Published msg: {}", message);
            channel.close();
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
        } catch (InterruptedException | IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consumerMsg(String exchange,
                            String queueName,
                            String routingKey) {
        try {
            // Create connection
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            // Create message channel
            final Channel channel = connection.createChannel();
            // Declare message queue
            channel.queueDeclare(queueName, true, false, false, null);
            // Bind queue to exchange
            channel.queueBind(queueName, exchange, routingKey);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    log.info("Consumed msg: {}", message);

                    // Get Rabbitmq message and save it to the database
                    // Note: This is just an example. If there are multiple types of messages, they can be handled using if...else or factory + strategy pattern for complex cases
                    notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);

                    // Acknowledge the message
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            // Disable auto-acknowledgment
            channel.basicConsume(queueName, false, consumer);
            channel.close();
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
        } catch (InterruptedException | IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processConsumerMsg() {
        log.info("Begin processing consumer messages.");

        Integer stepTotal = 1;
        Integer step = 0;

        // TODO: This approach is not optimal and will be refactored into blocking I/O mode later
        while (true) {
            step++;
            try {
                log.info("Consumer message processing cycle.");
                consumerMsg(CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUEUE_NAME_PRAISE,
                        CommonConstants.QUEUE_KEY_PRAISE);
                if (step.equals(stepTotal)) {
                    Thread.sleep(10000);
                    step = 0;
                }
            } catch (Exception e) {
                // Handle exceptions
            }
        }
    }
}


package com.manual.manual.messaging;

import com.manual.manual.config.OrderProperties;
import com.manual.manual.constant.OrderRabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class OrderDelayMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final OrderProperties orderProperties;

    public OrderDelayMessageService(RabbitTemplate rabbitTemplate, OrderProperties orderProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderProperties = orderProperties;
    }

    public void sendOrderCancelDelay(Long orderId) {
        if (orderId == null) {
            return;
        }
        Runnable sender = () -> {
            try {
                rabbitTemplate.convertAndSend(
                        OrderRabbitConstant.ORDER_DELAY_EXCHANGE,
                        OrderRabbitConstant.ORDER_DELAY_ROUTING_KEY,
                        String.valueOf(orderId),
                        message -> {
                            message.getMessageProperties().setExpiration(String.valueOf(orderProperties.getTimeoutMillis()));
                            return message;
                        }
                );
            } catch (RuntimeException exception) {
                log.error("send order delay cancel message failed, orderId={}", orderId, exception);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sender.run();
                }
            });
            return;
        }
        sender.run();
    }
}

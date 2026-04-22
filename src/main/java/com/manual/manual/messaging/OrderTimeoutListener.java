package com.manual.manual.messaging;

import com.manual.manual.constant.OrderRabbitConstant;
import com.manual.manual.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderTimeoutListener {

    private final OrderService orderService;

    public OrderTimeoutListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = OrderRabbitConstant.ORDER_CANCEL_QUEUE)
    public void handleOrderTimeout(String message) {
        try {
            Long orderId = Long.valueOf(message);
            orderService.cancelTimeoutOrder(orderId);
        } catch (RuntimeException exception) {
            log.error("handle timeout order message failed, message={}", message, exception);
            throw exception;
        }
    }
}

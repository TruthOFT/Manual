package com.manual.manual.config;

import com.manual.manual.constant.OrderRabbitConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class OrderRabbitConfig {

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(OrderRabbitConstant.ORDER_DELAY_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange orderCancelExchange() {
        return new DirectExchange(OrderRabbitConstant.ORDER_CANCEL_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(OrderRabbitConstant.ORDER_DELAY_QUEUE)
                .deadLetterExchange(OrderRabbitConstant.ORDER_CANCEL_EXCHANGE)
                .deadLetterRoutingKey(OrderRabbitConstant.ORDER_CANCEL_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(OrderRabbitConstant.ORDER_CANCEL_QUEUE).build();
    }

    @Bean
    public Binding orderDelayBinding(@Qualifier("orderDelayQueue") Queue orderDelayQueue,
                                     @Qualifier("orderDelayExchange") DirectExchange orderDelayExchange) {
        return BindingBuilder.bind(orderDelayQueue)
                .to(orderDelayExchange)
                .with(OrderRabbitConstant.ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderCancelBinding(@Qualifier("orderCancelQueue") Queue orderCancelQueue,
                                      @Qualifier("orderCancelExchange") DirectExchange orderCancelExchange) {
        return BindingBuilder.bind(orderCancelQueue)
                .to(orderCancelExchange)
                .with(OrderRabbitConstant.ORDER_CANCEL_ROUTING_KEY);
    }
}

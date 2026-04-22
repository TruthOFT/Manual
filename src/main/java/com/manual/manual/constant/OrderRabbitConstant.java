package com.manual.manual.constant;

public final class OrderRabbitConstant {

    public static final String ORDER_DELAY_EXCHANGE = "manual.order.delay.exchange";

    public static final String ORDER_DELAY_QUEUE = "manual.order.delay.queue.v2";

    public static final String ORDER_DELAY_ROUTING_KEY = "manual.order.delay";

    public static final String ORDER_CANCEL_EXCHANGE = "manual.order.cancel.exchange";

    public static final String ORDER_CANCEL_QUEUE = "manual.order.cancel.queue";

    public static final String ORDER_CANCEL_ROUTING_KEY = "manual.order.cancel";

    private OrderRabbitConstant() {
    }
}

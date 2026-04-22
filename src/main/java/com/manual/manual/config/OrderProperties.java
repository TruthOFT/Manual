package com.manual.manual.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "manual.order")
public class OrderProperties {

    private int timeoutMinutes = 15;

    public int getTimeoutMillis() {
        return Math.max(1, timeoutMinutes) * 60 * 1000;
    }
}

package com.example.servicepoller.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("rest.client")
public class RestClientProperties {

    private int maxConnectionsPerRoute = 4;
    private int maxConnections = 16;
    private int connectionRequestTimeout = 5000;
    private int connectionTimeout = 5000;
    private int socketReadTimeout = 15000;
}

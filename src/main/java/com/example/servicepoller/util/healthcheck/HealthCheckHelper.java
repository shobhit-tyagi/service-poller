package com.example.servicepoller.util.healthcheck;

import com.example.servicepoller.api.v1.model.HealthCheck;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class HealthCheckHelper {

    private final RestTemplate restTemplate;

    public HealthCheck call(final String url) {
        try {
            val response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode().is2xxSuccessful() ? HealthCheck.OK : HealthCheck.FAIL;
        } catch (Exception ex) {
            return HealthCheck.FAIL;
        }
    }
}

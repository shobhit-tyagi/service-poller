package com.example.servicepoller.task;

import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import com.example.servicepoller.util.healthcheck.HealthCheckHelper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServicePoller {

    @NonNull
    private final ServiceManager serviceManager;
    @NonNull
    private final HealthCheckHelper healthCheckHelper;
    @Value("${service.poller.refresh-frequency-secs:60}")
    private long pollingFrequency;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        val lastModifiedBefore = Instant.now().minusSeconds(pollingFrequency);
        serviceManager.getAllForUpdateModifiedBefore(lastModifiedBefore)
                .forEach(service -> {
                    log.info("Running health check for  service "+ service.getName());
                    val healthCheck = healthCheckHelper.call(service.getUrl());
                    serviceManager.update(service.getId(), Service.builder()
                            .status(healthCheck)
                            .build());
                });

    }
}

package com.example.servicepoller.data.model;

import com.example.servicepoller.api.v1.model.HealthCheck;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Value
@Builder
public class Service {
    String id;
    @NotNull(message = "Name should not be null")
    @NotEmpty(message = "Name should not be empty")
    String name;
    @NotNull(message = "Health check url should not be null")
    @NotEmpty(message = "Health check url should not be empty")
    @URL(message = "Health check url should be valid")
    String url;
    HealthCheck status;
    Instant lastChecked;
    Instant createdAt;
}

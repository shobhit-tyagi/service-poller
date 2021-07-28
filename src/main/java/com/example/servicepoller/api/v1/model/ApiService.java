package com.example.servicepoller.api.v1.model;

import com.example.servicepoller.data.model.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiService {

    private String id;
    private String name;
    private String url;
    private String status;
    private Instant createdAt;
    private Instant lastChecked;

    public ApiService(final Service service) {
        this.id = service.getId();
        this.name = service.getName();
        this.url = service.getUrl();
        this.status = service.getStatus().name();
        this.createdAt = service.getCreatedAt();
        this.lastChecked = service.getLastChecked();
    }
}

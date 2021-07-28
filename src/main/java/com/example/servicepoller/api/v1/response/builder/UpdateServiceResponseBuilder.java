package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.UpdateServiceRequest;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateServiceResponseBuilder {

    private final ServiceManager serviceManager;

    public ResponseEntity<ApiService> update(final String id, final UpdateServiceRequest request) {
        val service = serviceManager.update(id, Service.builder()
                .name(request.getName())
                .url(request.getUrl())
                .build());
        return ResponseEntity.ok(new ApiService(service));
    }
}

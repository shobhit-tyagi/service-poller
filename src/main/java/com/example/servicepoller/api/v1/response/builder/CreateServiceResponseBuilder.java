package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.CreateServiceRequest;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import com.example.servicepoller.util.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CreateServiceResponseBuilder {

    private final ServiceManager serviceManager;

    public ResponseEntity<ApiService> create(final CreateServiceRequest request) {
        val service = serviceManager.create(Service.builder()
                .name(request.getName())
                .url(request.getUrl())
                .build());
        return ResponseEntity.ok(new ApiService(service));
    }
}

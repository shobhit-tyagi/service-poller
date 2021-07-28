package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.api.v1.model.ApiService;
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
public class GetServiceResponseBuilder {

    private final ServiceManager serviceManager;

    public ResponseEntity<List<ApiService>> getAll() {
        val services = serviceManager.getAll();
        return ResponseEntity.ok(services.stream()
                .map(ApiService::new)
                .collect(Collectors.toList()));
    }

    public ResponseEntity<ApiService> getOne(final String id) {
        val serviceOptional = serviceManager.getOne(id);
        if (serviceOptional.isEmpty()) {
            throw new NotFoundException("Request service with id "+ id+" could not be found");
        }
        return ResponseEntity.ok(new ApiService(serviceOptional.get()));
    }
}

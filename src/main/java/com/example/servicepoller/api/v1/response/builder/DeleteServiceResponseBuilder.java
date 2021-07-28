package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.CreateServiceRequest;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeleteServiceResponseBuilder {

    private final ServiceManager serviceManager;

    public ResponseEntity<Void> delete(final String id) {
        serviceManager.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.servicepoller.api.v1.controller;

import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.CreateServiceRequest;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.api.v1.model.UpdateServiceRequest;
import com.example.servicepoller.api.v1.response.builder.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController("ServiceControllerV1")
@RequestMapping("/v1/services")
public class ServiceController {

    private final GetServiceResponseBuilder getServiceResponseBuilder;
    private final CreateServiceResponseBuilder createServiceResponseBuilder;
    private final UpdateServiceResponseBuilder updateServiceResponseBuilder;
    private final DeleteServiceResponseBuilder deleteServiceResponseBuilder;

    @GetMapping
    public ResponseEntity<List<ApiService>> getAll() {
        return getServiceResponseBuilder.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiService> getOne(@PathVariable final String id) {
        return getServiceResponseBuilder.getOne(id);
    }

    @PostMapping
    public ResponseEntity<ApiService> create(@RequestBody final CreateServiceRequest body) {
        return createServiceResponseBuilder.create(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiService> update(@PathVariable final String id, @RequestBody final UpdateServiceRequest body) {
        return updateServiceResponseBuilder.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        return deleteServiceResponseBuilder.delete(id);
    }
}

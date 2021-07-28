package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.CreateServiceRequest;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateServiceResponseBuilderTest extends UnitTestBase {

    private CreateServiceResponseBuilder unitToTest;
    @Mock
    private ServiceManager serviceManager;

    @BeforeEach
    public void setup() {
        unitToTest = new CreateServiceResponseBuilder(serviceManager);
    }

    @Test
    public void testCreate() {
        val serviceName = "lord-of-the-rings-service";
        val url = "http://one-ring.com/ping";
        val serviceId = UUID.randomUUID().toString();
        val createdAt = Instant.now();
        val lastChecked = Instant.now();
        when(serviceManager.create(any()))
                .thenReturn(Service.builder()
                        .id(serviceId)
                        .name(serviceName)
                        .url(url)
                        .status(HealthCheck.FAIL)
                        .createdAt(createdAt)
                        .lastChecked(lastChecked)
                        .build());

        val response = unitToTest.create(CreateServiceRequest.builder()
                .name(serviceName)
                .url(url)
                .build());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApiService.builder()
                .id(serviceId)
                .name(serviceName)
                .url(url)
                .createdAt(createdAt)
                .status(HealthCheck.FAIL.name())
                .lastChecked(lastChecked)
                .build(), response.getBody());
        verify(serviceManager).create(Service.builder()
                .name(serviceName)
                .url(url)
                .build());
    }
}

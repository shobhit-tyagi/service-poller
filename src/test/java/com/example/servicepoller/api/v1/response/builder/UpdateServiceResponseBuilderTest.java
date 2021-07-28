package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.api.v1.model.UpdateServiceRequest;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateServiceResponseBuilderTest extends UnitTestBase {

    private UpdateServiceResponseBuilder unitToTest;
    @Mock
    private ServiceManager serviceManager;

    @BeforeEach
    public void setup() {
        unitToTest = new UpdateServiceResponseBuilder(serviceManager);
    }

    @Test
    public void testUpdate() {
        val serviceName = "lord-of-the-rings-service";
        val team = "elves";
        val url = "http://one-ring.com/ping";
        val serviceId = UUID.randomUUID().toString();
        val createdAt = Instant.now();
        val lastChecked = Instant.now();
        when(serviceManager.update(anyString(), any()))
                .thenReturn(Service.builder()
                        .id(serviceId)
                        .name(serviceName)
                        .url(url)
                        .status(HealthCheck.FAIL)
                        .createdAt(createdAt)
                        .lastChecked(lastChecked)
                        .build());

        val response = unitToTest.update(serviceId, UpdateServiceRequest.builder()
                .name(serviceName)
                .url(url)
                .build());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApiService.builder()
                .id(serviceId)
                .name(serviceName)
                .url(url)
                .status(HealthCheck.FAIL.name())
                .createdAt(createdAt)
                .lastChecked(lastChecked)
                .build(), response.getBody());
        verify(serviceManager).update(serviceId, Service.builder()
                .name(serviceName)
                .url(url)
                .build());
    }
}

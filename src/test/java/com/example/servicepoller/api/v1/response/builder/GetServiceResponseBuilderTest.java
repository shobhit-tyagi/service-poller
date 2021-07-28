package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import com.example.servicepoller.util.exception.NotFoundException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetServiceResponseBuilderTest extends UnitTestBase {

    private GetServiceResponseBuilder unitToTest;
    @Mock
    private ServiceManager serviceManager;

    @BeforeEach
    public void setup() {
        unitToTest = new GetServiceResponseBuilder(serviceManager);
    }

    @Test
    public void testGetAll() {
        val service1 = createTestService("lord-of-the-rings-service", "http://one-ring.com/ping");
        val service2 = createTestService("hobbit-service", "http://frodo.com/ping");
        when(serviceManager.getAll())
                .thenReturn(List.of(service1, service2));

        val response = unitToTest.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().containsAll(List.of(ApiService.builder()
                        .id(service1.getId())
                        .name(service1.getName())
                        .url(service1.getUrl())
                        .status(HealthCheck.FAIL.name())
                        .createdAt(service1.getCreatedAt())
                        .lastChecked(service1.getLastChecked())
                        .build(),
                ApiService.builder()
                        .id(service2.getId())
                        .name(service2.getName())
                        .url(service2.getUrl())
                        .status(HealthCheck.FAIL.name())
                        .createdAt(service2.getCreatedAt())
                        .lastChecked(service2.getLastChecked())
                        .build())));
    }

    @Test
    public void testGetOne_notFound() {
        assertThrows(NotFoundException.class, () -> {
            unitToTest.getOne("unknown-id");
        });
    }

    @Test
    public void testGetOne() {
        val service = createTestService("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(serviceManager.getOne(service.getId()))
                .thenReturn(Optional.of(service));

        val response = unitToTest.getOne(service.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiService.builder()
                .id(service.getId())
                .name(service.getName())
                .url(service.getUrl())
                .status(HealthCheck.FAIL.name())
                .createdAt(service.getCreatedAt())
                .lastChecked(service.getLastChecked())
                .build(), response.getBody());
    }

    private Service createTestService(final String serviceName,
                                      final String url) {
        return Service.builder()
                .id(UUID.randomUUID().toString())
                .name(serviceName)
                .url(url)
                .status(HealthCheck.FAIL)
                .createdAt(Instant.now())
                .lastChecked(Instant.now())
                .build();
    }
}

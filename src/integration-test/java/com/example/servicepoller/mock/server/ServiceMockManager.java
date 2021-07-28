package com.example.servicepoller.mock.server;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.mockserver.matchers.Times.once;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceMockManager {

    private final Map<MockService, ServiceMock> serviceMockMap = new HashMap<>();

    @PostConstruct
    public void postConstruct() {

        for (final MockService service : MockService.values()) {

            createServiceMockFor(service);
        }
    }

    private void createServiceMockFor(final MockService service) {

        log.info("Creating ServiceMock for {}", service.getId());

        val serviceMock = new ServiceMock(service);
        serviceMockMap.put(service, serviceMock);
    }

    public ClientAndServer serverFor(final MockService service) {

        return serviceMockMap.get(service)
                             .getMockServer();
    }

    public void reset() {

        serviceMockMap.entrySet()
                      .stream()
                      .map(Entry::getValue)
                      .map(ServiceMock.class::cast)
                      .forEach(ServiceMock::reset);
    }

}

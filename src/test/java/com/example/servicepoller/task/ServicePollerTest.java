package com.example.servicepoller.task;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import com.example.servicepoller.util.healthcheck.HealthCheckHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicePollerTest extends UnitTestBase {

    private ServicePoller unitToTest;
    @Mock
    private ServiceManager serviceManager;
    @Mock
    private HealthCheckHelper healthCheckHelper;

    @BeforeEach
    public void setup() {
        unitToTest = new ServicePoller(serviceManager, healthCheckHelper);
    }

    @Test
    public void testPoll() {
        when(serviceManager.getAllForUpdateModifiedBefore(any()))
                .thenReturn(List.of(Service.builder()
                                .id("lord-of-the-rings-service")
                                .url("http://lor")
                                .build(),
                        Service.builder()
                                .id("hobbit-service")
                                .url("http://hobbit")
                                .build()));
        when(healthCheckHelper.call("http://lor")).thenReturn(HealthCheck.FAIL);
        when(healthCheckHelper.call("http://hobbit")).thenReturn(HealthCheck.OK);

        unitToTest.poll();
        verify(healthCheckHelper).call("http://lor");
        verify(healthCheckHelper).call("http://hobbit");
        verify(serviceManager).update("lord-of-the-rings-service", Service.builder()
                .status(HealthCheck.FAIL)
                .build());
        verify(serviceManager).update("hobbit-service", Service.builder()
                .status(HealthCheck.OK)
                .build());
    }
}

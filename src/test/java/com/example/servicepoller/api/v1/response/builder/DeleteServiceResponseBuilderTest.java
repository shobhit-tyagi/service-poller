package com.example.servicepoller.api.v1.response.builder;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.ApiService;
import com.example.servicepoller.api.v1.model.CreateServiceRequest;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.manager.ServiceManager;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteServiceResponseBuilderTest extends UnitTestBase {

    private DeleteServiceResponseBuilder unitToTest;
    @Mock
    private ServiceManager serviceManager;

    @BeforeEach
    public void setup() {
        unitToTest = new DeleteServiceResponseBuilder(serviceManager);
    }

    @Test
    public void testDelete() {
        val serviceId = UUID.randomUUID().toString();
        val response = unitToTest.delete(serviceId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(serviceManager).delete(serviceId);
    }
}

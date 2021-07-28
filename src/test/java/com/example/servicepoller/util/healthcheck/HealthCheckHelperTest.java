package com.example.servicepoller.util.healthcheck;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.HealthCheck;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class HealthCheckHelperTest extends UnitTestBase {

    private HealthCheckHelper unitToTest;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        unitToTest = new HealthCheckHelper(restTemplate);
    }

    @Test
    public void testCall_exception() {
        val url = "http://temp";
        when(restTemplate.getForEntity(url, Void.class))
                .thenThrow(new RuntimeException());

        val response = unitToTest.call(url);
        assertEquals(HealthCheck.FAIL, response);
    }

    @Test
    public void testCall_non2xx() {
        val url = "http://temp";
        when(restTemplate.getForEntity(url, Void.class))
                .thenReturn(ResponseEntity.badRequest().build());

        val response = unitToTest.call(url);
        assertEquals(HealthCheck.FAIL, response);
    }

    @Test
    public void testCall_2xx() {
        val url = "http://temp";
        when(restTemplate.getForEntity(url, Void.class))
                .thenReturn(ResponseEntity.ok().build());

        val response = unitToTest.call(url);
        assertEquals(HealthCheck.OK, response);
    }
}

package com.example.servicepoller.manager;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.data.entity.ServiceEntity;
import com.example.servicepoller.data.entity.ServiceRepository;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.data.model.ServiceMapper;
import com.example.servicepoller.util.healthcheck.HealthCheckHelper;
import com.example.servicepoller.util.exception.BadRequestException;
import com.example.servicepoller.util.exception.NotFoundException;
import com.example.servicepoller.validator.ServiceValidator;
import com.example.servicepoller.validator.ValidationResult;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServiceManagerTest extends UnitTestBase {

    private ServiceManager unitToTest;
    @Mock
    private ServiceRepository repository;
    @Mock
    private ServiceValidator validator;
    @Mock
    private HealthCheckHelper healthCheckHelper;

    @BeforeEach
    public void setup() {
        unitToTest = new ServiceManagerImpl(repository,
                new ServiceMapper(), // Not mocking to test the real mapping
                validator,
                healthCheckHelper);
    }

    @Test
    public void testGetOne() {
        val serviceEntity = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(repository.findById(serviceEntity.getId()))
                .thenReturn(Optional.of(serviceEntity));

        val response = unitToTest.getOne(serviceEntity.getId());
        assertTrue(response.isPresent());
        assertEquals(createTestServiceFromEntity(serviceEntity), response.get());
    }

    @Test
    public void testGetAll() {
        val serviceEntity1 = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        val serviceEntity2 = createTestServiceEntity("hobbit-service", "http://frodo.com/ping");
        when(repository.findAll()).thenReturn(List.of(serviceEntity1, serviceEntity2));

        val response = unitToTest.getAll();
        assertEquals(2, response.size());
        assertTrue(response.containsAll(List.of(createTestServiceFromEntity(serviceEntity1),
                createTestServiceFromEntity(serviceEntity2))));
    }

    @Test
    public void testGetAllForUpdateModifiedBefore() {
        val serviceEntity1 = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        val serviceEntity2 = createTestServiceEntity("hobbit-service", "http://frodo.com/ping");
        val lastModifiedBefore = Instant.now();
        when(repository.findAllForUpdateModifiedBefore(lastModifiedBefore))
                .thenReturn(List.of(serviceEntity1, serviceEntity2));

        val response = unitToTest.getAllForUpdateModifiedBefore(lastModifiedBefore);
        assertEquals(2, response.size());
        assertTrue(response.containsAll(List.of(createTestServiceFromEntity(serviceEntity1),
                createTestServiceFromEntity(serviceEntity2))));
    }

    @Test
    public void testCreate_hasErrors() {
        val serviceInput = Service.builder().build();
        val validationResult = new ValidationResult();
        validationResult.addError("gimli", "Gimli is not a hobbit");
        when(validator.validate(serviceInput)).thenReturn(validationResult);

        assertThrows(BadRequestException.class, () -> {
            unitToTest.create(serviceInput);
        });
    }

    @Test
    public void testCreate_hasWarnings_success() {
        val serviceName = "lord-of-the-rings-service";
        val url = "http://one-ring.com/ping";
        val serviceInput = Service.builder()
                .name(serviceName)
                .url(url)
                .build();
        val validationResult = new ValidationResult();
        validationResult.addWarning("gandalf", "Gandalf is too tall");
        when(validator.validate(serviceInput)).thenReturn(validationResult);
        val createdEntity = createTestServiceEntity(serviceName, url);
        when(repository.save(any())).thenReturn(createdEntity);
        when(healthCheckHelper.call(url)).thenReturn(HealthCheck.OK);
        val response = unitToTest.create(serviceInput);
        assertEquals(createTestServiceFromEntity(createdEntity), response);

        // These fields are set by the database directly
        createdEntity.setId(null);
        createdEntity.setCreatedAt(null);
        val argCaptor = ArgumentCaptor.forClass(ServiceEntity.class);
        verify(repository).save(argCaptor.capture());
        assertNotNull(argCaptor.getValue().getLastChecked());
        val savedEntity = argCaptor.getValue();
        createdEntity.setLastChecked(savedEntity.getLastChecked());
        assertEquals(savedEntity, createdEntity);
        verify(validator).validate(serviceInput);
        verify(validator).validateName(serviceName);
    }

    @Test
    public void testUpdate_notFound() {
        assertThrows(NotFoundException.class, () -> {
            unitToTest.update("unknown-id", Service.builder().build());
        });
    }

    @Test
    public void testUpdate_nothingModified() {
        val serviceEntity = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(repository.findById(serviceEntity.getId()))
                .thenReturn(Optional.of(serviceEntity));

        unitToTest.update(serviceEntity.getId(), Service.builder()
                .name("lord-of-the-rings-service")
                .build());
        verify(validator, never()).validateName(any());
        verify(repository, never()).save(any());
    }

    @Test
    public void testUpdate_hasErrors() {
        val serviceEntity = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(repository.findById(serviceEntity.getId()))
                .thenReturn(Optional.of(serviceEntity));
        val validationResult = new ValidationResult();
        validationResult.addError("gimli", "Gimli is not a hobbit");
        when(validator.validate(any())).thenReturn(validationResult);

        assertThrows(BadRequestException.class, () -> {
            unitToTest.update(serviceEntity.getId(), Service.builder()
                    .url("http")
                    .build());
        });
    }

    @Test
    public void testUpdate_multipleModified() {
        val serviceEntity = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(repository.findById(serviceEntity.getId()))
                .thenReturn(Optional.of(serviceEntity));
        val updatedServiceEntity = createTestServiceEntity("hobbit-service", "http://frodo.com/ping");
        updatedServiceEntity.setId(serviceEntity.getId());
        when(repository.save(any())).thenReturn(updatedServiceEntity);
        val validationResult = new ValidationResult();
        validationResult.addWarning("gandalf", "Gandalf is too tall");
        when(validator.validate(any())).thenReturn(validationResult);
        when(healthCheckHelper.call("http://frodo.com/ping")).thenReturn(HealthCheck.OK);
        val response = unitToTest.update(serviceEntity.getId(), Service.builder()
                .name("hobbit-service")
                .url("http://frodo.com/ping")
                .build());
        assertEquals(createTestServiceFromEntity(updatedServiceEntity),
                response);

        verify(healthCheckHelper).call("http://frodo.com/ping");
        verify(validator).validateName("hobbit-service");
        serviceEntity.setName("hobbit-service");
        serviceEntity.setUrl("http://frodo.com/ping");
        verify(repository).save(serviceEntity);
    }

    @Test
    public void testDelete_notFound() {
        assertThrows(NotFoundException.class, () -> {
            unitToTest.delete("unknown-id");
        });
    }

    @Test
    public void testDelete_success() {
        val serviceEntity = createTestServiceEntity("lord-of-the-rings-service", "http://one-ring.com/ping");
        when(repository.findById(serviceEntity.getId()))
                .thenReturn(Optional.of(serviceEntity));

        unitToTest.delete(serviceEntity.getId());
        verify(repository).delete(serviceEntity);
    }

    private ServiceEntity createTestServiceEntity(final String serviceName,
                                                  final String url) {
        return ServiceEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(serviceName)
                .url(url)
                .status(HealthCheck.OK.name())
                .createdAt(Instant.now())
                .lastChecked(Instant.now())
                .build();
    }

    private Service createTestServiceFromEntity(final ServiceEntity serviceEntity) {
        return Service.builder()
                .id(serviceEntity.getId())
                .name(serviceEntity.getName())
                .url(serviceEntity.getUrl())
                .status(HealthCheck.OK)
                .createdAt(serviceEntity.getCreatedAt())
                .lastChecked(serviceEntity.getLastChecked())
                .build();
    }
}

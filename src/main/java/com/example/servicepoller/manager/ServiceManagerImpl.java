package com.example.servicepoller.manager;

import com.example.servicepoller.data.entity.ServiceRepository;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.data.model.ServiceMapper;
import com.example.servicepoller.util.healthcheck.HealthCheckHelper;
import com.example.servicepoller.util.exception.BadRequestException;
import com.example.servicepoller.util.exception.NotFoundException;
import com.example.servicepoller.validator.ServiceValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ServiceManagerImpl implements ServiceManager {

    private final ServiceRepository repository;
    private final ServiceMapper mapper;
    private final ServiceValidator validator;
    private final HealthCheckHelper healthCheckHelper;

    @Override
    public Optional<Service> getOne(String id) {
        return repository.findById(id)
                .map(mapper::toPresentation);
    }

    @Override
    public List<Service> getAll() {
        return Streamable.of(repository.findAll())
                .toList()
                .stream()
                .map(mapper::toPresentation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Service> getAllForUpdateModifiedBefore(final Instant lastModifiedBefore) {
        return repository.findAllForUpdateModifiedBefore(lastModifiedBefore)
                .stream()
                .map(mapper::toPresentation)
                .collect(Collectors.toList());
    }

    @Override
    public Service create(final Service service) {
        val validationResult = validator.validate(service);
        if (validationResult.hasErrors()) {
            throw new BadRequestException("Failed to create a service", validationResult.getErrors());
        }
        if (validationResult.hasWarnings()) {
            log.warn("Creating a service with some warnings: {}", validationResult.hasWarnings());
        }
        validator.validateName(service.getName());
        val entity = mapper.toEntity(service);
        val status = healthCheckHelper.call(service.getUrl());
        entity.setStatus(status.name());
        entity.setLastChecked(Instant.now());
        return mapper.toPresentation(repository.save(entity));
    }

    @Override
    // Patch will only consider non null values
    public Service update(final String id, final Service service) {
        val entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new NotFoundException("Request service with id "+ id+" could not be found");
        }
        boolean modified = false;
        boolean validateNameForUniqueness = false;
        boolean healthCheckRequired = false;
        val entity = entityOptional.get();
        if (service.getName() != null
                && !service.getName().equals(entity.getName())) {
            entity.setName(service.getName());
            validateNameForUniqueness = true;
            modified = true;
        }
        if (service.getUrl() != null
                && !service.getUrl().equals(entity.getUrl())) {
            entity.setUrl(service.getUrl());
            modified = true;
            healthCheckRequired = true;
        }
        if (service.getStatus() != null) {
            entity.setStatus(service.getStatus().name());
            entity.setLastChecked(Instant.now());
            modified = true;
            healthCheckRequired = false;
        }
        if (modified) {
            val validationResult = validator.validate(mapper.toPresentation(entity));
            if (validationResult.hasErrors()) {
                throw new BadRequestException("Failed to update the service", validationResult.getErrors());
            }
            if (validationResult.hasWarnings()) {
                log.warn("Updating the service with some warnings: {}", validationResult.hasWarnings());
            }
            if (validateNameForUniqueness) {
                validator.validateName(service.getName());
            }
            if (healthCheckRequired) {
                val status = healthCheckHelper.call(service.getUrl());
                entity.setStatus(status.name());
            }
            val updatedEntity = repository.save(entity);
            return mapper.toPresentation(updatedEntity);
        }
        return mapper.toPresentation(entity);
    }

    @Override
    public void delete(final String id) {
        val entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new NotFoundException("Request service with id "+ id+" could not be found");
        }
        repository.delete(entityOptional.get());
    }
}

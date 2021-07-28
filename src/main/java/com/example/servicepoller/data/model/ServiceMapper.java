package com.example.servicepoller.data.model;

import com.example.servicepoller.api.v1.model.HealthCheck;
import com.example.servicepoller.data.entity.ServiceEntity;
import com.example.servicepoller.util.data.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper implements Mapper<ServiceEntity, Service> {

    @Override
    public ServiceEntity toEntity(final Service service) {
        return ServiceEntity.builder()
                .name(service.getName())
                .url(service.getUrl())
                .build();
    }

    @Override
    public Service toPresentation(final ServiceEntity serviceEntity) {
        return Service.builder()
                .id(serviceEntity.getId())
                .name(serviceEntity.getName())
                .url(serviceEntity.getUrl())
                .status(HealthCheck.valueOf(serviceEntity.getStatus()))
                .createdAt(serviceEntity.getCreatedAt())
                .lastChecked(serviceEntity.getLastChecked())
                .build();
    }
}

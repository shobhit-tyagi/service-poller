package com.example.servicepoller.manager;

import com.example.servicepoller.data.model.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ServiceManager {

    Optional<Service> getOne(String id);
    List<Service> getAll();
    List<Service> getAllForUpdateModifiedBefore(Instant lastModifiedBefore);
    Service create(Service service);
    Service update(String id, Service service);
    void delete(String id);
}

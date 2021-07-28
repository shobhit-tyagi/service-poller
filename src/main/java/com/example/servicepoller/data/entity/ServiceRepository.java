package com.example.servicepoller.data.entity;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends CrudRepository<ServiceEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select s from ServiceEntity s where s.lastChecked < :lastModifiedBefore")
    List<ServiceEntity> findAllForUpdateModifiedBefore(Instant lastModifiedBefore);
    Optional<ServiceEntity> findByName(String name);
}

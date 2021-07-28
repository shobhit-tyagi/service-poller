package com.example.servicepoller.validator;

import com.example.servicepoller.data.entity.ServiceRepository;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.util.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class ServiceValidator extends Validator<Service> {

    private final ServiceRepository repository;

    public ServiceValidator(final @Autowired LocalValidatorFactoryBean localValidatorFactoryBean,
                            final @Autowired ServiceRepository repository) {
        super(localValidatorFactoryBean);
        this.repository = repository;
    }

    public void validateName(final String name) {
        if (repository.findByName(name).isPresent()) {
            throw new BadRequestException("Service with name "+name+" already exists");
        }
    }
}

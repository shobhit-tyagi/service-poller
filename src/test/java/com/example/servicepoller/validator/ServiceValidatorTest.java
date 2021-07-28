package com.example.servicepoller.validator;

import com.example.servicepoller.UnitTestBase;
import com.example.servicepoller.data.entity.ServiceEntity;
import com.example.servicepoller.data.entity.ServiceRepository;
import com.example.servicepoller.data.model.Service;
import com.example.servicepoller.util.exception.BadRequestException;
import lombok.val;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceValidatorTest extends UnitTestBase {

    private ServiceValidator unitToTest;
    @Mock
    private LocalValidatorFactoryBean localValidatorFactoryBean;
    @Mock
    private ServiceRepository repository;

    @BeforeEach
    public void setup() {
        unitToTest = new ServiceValidator(localValidatorFactoryBean,
                repository);
    }

    @Test
    public void testValidate() {
        val serviceInput = Service.builder().build();
        val constraintViolationMock = mock(ConstraintViolation.class);
        when(constraintViolationMock.getPropertyPath()).thenReturn(PathImpl.createPathFromString("mordor"));
        when(constraintViolationMock.getMessage()).thenReturn("Samwise is a hobbit");
        when(localValidatorFactoryBean.validate(serviceInput))
                .thenReturn(Set.of(constraintViolationMock));

        val response = unitToTest.validate(serviceInput);
        assertTrue(response.hasErrors());
        assertEquals(List.of("Samwise is a hobbit"), response.getErrors().get("mordor"));
        verify(localValidatorFactoryBean).validate(serviceInput);
    }

    @Test
    public void testValidateName_isNotUnique() {
        when(repository.findByName("legolas"))
                .thenReturn(Optional.of(ServiceEntity.builder().build()));
        assertThrows(BadRequestException.class, () -> {
            unitToTest.validateName("legolas");
        });
    }

    @Test
    public void testValidateName_success() {
        unitToTest.validateName("legolas");
    }
}

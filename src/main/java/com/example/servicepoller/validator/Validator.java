package com.example.servicepoller.validator;

import lombok.val;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public abstract class Validator<T> {

    private LocalValidatorFactoryBean localValidatorFactoryBean;

    public Validator(final LocalValidatorFactoryBean localValidatorFactoryBean) {
        this.localValidatorFactoryBean = localValidatorFactoryBean;
    }

    public ValidationResult validate(final T t) {
        val result = new ValidationResult();
        val violations = localValidatorFactoryBean.validate(t);
        if (CollectionUtils.isEmpty(violations)) {
            return result;
        }
        for (val violation : violations) {

            result.addError(((PathImpl) violation.getPropertyPath()).getLeafNode()
                            .getName(),
                    violation.getMessage());
        }
        return result;
    }
}

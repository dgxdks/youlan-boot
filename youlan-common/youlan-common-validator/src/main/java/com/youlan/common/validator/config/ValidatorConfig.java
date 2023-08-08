package com.youlan.common.validator.config;

import lombok.AllArgsConstructor;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
@AllArgsConstructor
public class ValidatorConfig {
    private final MessageSource messageSource;

    @Bean
    @ConditionalOnMissingBean(value = Validator.class)
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                .messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource)))
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }

    @Bean
    @ConditionalOnMissingBean(value = MethodValidationPostProcessor.class)
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }
}

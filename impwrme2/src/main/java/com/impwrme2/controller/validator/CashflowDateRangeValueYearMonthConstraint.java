package com.impwrme2.controller.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CashflowDateRangeValueYearMonthValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CashflowDateRangeValueYearMonthConstraint {

	String message() default "{msg.validation.unknownError}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

package org.example.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.validation.CourseNameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CourseNameValidator.class)
public @interface ValidCourseName {
    String message() default CourseNameValidator.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.validation.constraint.ValidCourseName;

import java.util.Arrays;

public class CourseNameValidator implements ConstraintValidator<ValidCourseName, String> {
    public static final String[] COURSE_NAME_PREFIXES = new String[]{"FR_", "CPL_"};
    public static final String DEFAULT_MESSAGE = """
            Course name must start with "CPL_" or "FR_"
            """;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && Arrays.stream(COURSE_NAME_PREFIXES).anyMatch(s::startsWith);
    }
}

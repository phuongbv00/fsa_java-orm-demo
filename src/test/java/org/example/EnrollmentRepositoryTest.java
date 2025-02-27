package org.example;

import org.example.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class EnrollmentRepositoryTest {
    private final Logger logger = Logger.getLogger(EnrollmentRepositoryTest.class.getName());

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    public void processEnrollments() {
        assertDoesNotThrow(() -> {
            int processed = enrollmentRepository.processEnrollments(10);
            logger.info("Enrollments processed: " + processed);
        });
    }
}

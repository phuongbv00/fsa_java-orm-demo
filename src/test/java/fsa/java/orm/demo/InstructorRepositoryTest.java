package fsa.java.orm.demo;

import fsa.java.orm.demo.repository.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class InstructorRepositoryTest {
    private final Logger logger = Logger.getLogger(InstructorRepositoryTest.class.getName());

    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    public void processEnrollments() {
        assertDoesNotThrow(() -> {
            var instructorNameOnlyList = instructorRepository.search("N");
            logger.info(instructorNameOnlyList.toString());
        });
    }
}

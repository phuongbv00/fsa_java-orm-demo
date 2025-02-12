package org.example;

import org.example.repository.CourseRepository;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class RepositoryTest {
    private final Logger logger = Logger.getLogger(RepositoryTest.class.getName());

    @Autowired
    @Qualifier("courseJpaRepository")
    CourseRepository courseJpaRepository;

    @Autowired
    @Qualifier("courseHibernateRepository")
    CourseRepository courseHibernateRepository;

    @Autowired
    @Qualifier("courseJdbcRepository")
    CourseRepository courseJdbcRepository;

    private CourseRepository[] getRepositories() {
        return new CourseRepository[]{courseJpaRepository, courseHibernateRepository, courseJdbcRepository};
    }

    @ParameterizedTest
    @MethodSource("getRepositories")
    public void findAll(CourseRepository courseRepository) {
        var rs = courseRepository.findAll();
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("getRepositories")
    public void findAllWithPage(CourseRepository courseRepository) {
        var rs = courseRepository.findAll(2, 1);
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("getRepositories")
    public void getCourseStats(CourseRepository courseRepository) {
        var rs = courseRepository.getCourseStats();
        logger.info("result: " + rs);
        assertNotNull(rs);
    }
}

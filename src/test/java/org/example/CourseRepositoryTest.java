package org.example;

import org.example.model.entity.Course;
import org.example.model.entity.Instructor;
import org.example.repository.CourseRepository;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CourseRepositoryTest {
    private final Logger logger = Logger.getLogger(CourseRepositoryTest.class.getName());

    @Autowired
    @Qualifier("courseJdbcRepository")
    CourseRepository courseJdbcRepository;

    @Autowired
    @Qualifier("courseJpaRepository")
    CourseRepository courseJpaRepository;

    @Autowired
    @Qualifier("courseHibernateRepository")
    CourseRepository courseHibernateRepository;

    private Stream<Arguments> provideRepositories() {
        return Stream.of(
                Arguments.of(courseJdbcRepository),
                Arguments.of(courseJpaRepository),
                Arguments.of(courseHibernateRepository)
        );
    }

    private Stream<Arguments> provideRepositoriesWithOptions() {
        return Stream.of(
                Arguments.of(courseJdbcRepository, new int[]{1}),
                Arguments.of(courseJpaRepository, new int[]{1, 2, 3, 4, 5}),
                Arguments.of(courseHibernateRepository, new int[]{1})
        ).flatMap(arg -> {
            CourseRepository repository = (CourseRepository) arg.get()[0];
            int[] options = (int[]) arg.get()[1];
            return Arrays.stream(options).mapToObj(option -> Arguments.of(repository, option));
        });
    }

    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void findAll(CourseRepository courseRepository) {
        var rs = courseRepository.findAll();
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void findAllWithPage(CourseRepository courseRepository) {
        var rs = courseRepository.findAll(2, 1);
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("provideRepositoriesWithOptions")
    public void getCourseStats(CourseRepository courseRepository, int opt) {
        var rs = courseRepository.getCourseStats(opt);
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void save(CourseRepository courseRepository) {
        var random = new Random();
        var now = Instant.now();
        var instructor = new Instructor();
        instructor.setId(1);
        var course = new Course();
        course.setName("test_" + now.getEpochSecond() + "_" + random.nextInt());
        course.setCapacity(50);
        course.setStartDate(now.plus(7, ChronoUnit.DAYS));
        course.setEndDate(now.plus(45, ChronoUnit.DAYS));
        course.setInstructor(instructor);
        assertDoesNotThrow(() -> courseRepository.save(course));
    }
}

package org.example;

import org.example.model.dto.CourseSearchReq;
import org.example.model.entity.Course;
import org.example.model.entity.Instructor;
import org.example.repository.CourseRepository;
import org.example.repository.impl.CourseJdbcRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseRepositoryTest {
    private final Logger logger = Logger.getLogger(CourseRepositoryTest.class.getName());

    private final Map<CourseRepository, Integer> savedCourseIds = new HashMap<>();

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
                Arguments.of(courseJpaRepository, new int[]{1, 2, 3, 4, 5, 6}),
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
    @MethodSource("provideRepositories")
    public void findById(CourseRepository courseRepository) {
        var rs = courseRepository.findById(1);
        logger.info("result: " + rs);
        assertTrue(rs.isPresent());
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
    public void findByCriteria(CourseRepository courseRepository) {
        var criteria = new CourseSearchReq(
                "J",
                0,
                100,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-12-01T00:00:00Z"),
                1
        );
        var rs = courseRepository.findByCriteria(criteria);
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void findByCriteria_JpaOptionalCriteriaWellHandling(CourseRepository courseRepository) {
        if (courseRepository instanceof CourseJdbcRepository) {
            return;
        }
        var criteria = new CourseSearchReq(
                null,
                0,
                100,
                Instant.parse("2025-01-01T00:00:00Z"),
                null,
                null
        );
        var rs = courseRepository.findByCriteria(criteria);
        logger.info("result: " + rs);
        assertNotNull(rs);
    }

    @Test
    public void findByCriteria_JdbcOptionalCriteriaError() {
        var criteria = new CourseSearchReq(
                null,
                0,
                100,
                Instant.parse("2025-01-01T00:00:00Z"),
                null,
                null
        );
        assertThrows(Exception.class, () -> courseJdbcRepository.findByCriteria(criteria));
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void save(CourseRepository courseRepository) {
        var random = new Random();
        var now = Instant.now();
        var instructor = new Instructor();
        instructor.setId(1);
        var course = new Course();
        course.setName("test_" + courseRepository.getClass().getSimpleName() + "_save_" + now.getEpochSecond() + "_" + random.nextInt(100));
        course.setCapacity(random.nextInt(50));
        course.setStartDate(now.plus(7, ChronoUnit.DAYS));
        course.setEndDate(now.plus(45, ChronoUnit.DAYS));
        course.setInstructor(instructor);
        assertDoesNotThrow(() -> {
            var newCourse = courseRepository.save(course);
            assertNotNull(newCourse);
            savedCourseIds.put(courseRepository, newCourse.getId());
            logger.info("saved: " + newCourse);
        });
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void update(CourseRepository courseRepository) {
        var random = new Random();
        var now = Instant.now();
        var instructor = new Instructor();
        instructor.setId(1);
        var course = new Course();
        course.setId(savedCourseIds.get(courseRepository));
        course.setName("test_" + courseRepository.getClass().getSimpleName() + "_update_" + now.getEpochSecond() + "_" + random.nextInt(100));
        course.setCapacity(random.nextInt(50));
        course.setStartDate(now.plus(7, ChronoUnit.DAYS));
        course.setEndDate(now.plus(45, ChronoUnit.DAYS));
        course.setInstructor(instructor);
        assertDoesNotThrow(() -> {
            var updatedCourse = courseRepository.update(course);
            assertNotNull(updatedCourse);
            logger.info("updated: " + updatedCourse);
        });
    }

    @Order(3)
    @ParameterizedTest
    @MethodSource("provideRepositories")
    public void delete(CourseRepository courseRepository) {
        assertDoesNotThrow(() -> {
            int id = savedCourseIds.get(courseRepository);
            courseRepository.delete(id);
            logger.info("deleted: " + id);
        });
    }
}

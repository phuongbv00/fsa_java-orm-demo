package org.example;

import org.example.repository.CourseRepository;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class RepositoryTest {
    private final Logger logger = Logger.getLogger(RepositoryTest.class.getName());

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
                Arguments.of(courseJpaRepository, new int[]{1, 2, 3, 4}),
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
}

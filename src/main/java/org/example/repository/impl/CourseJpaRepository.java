package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.example.config.db.JPAClient;
import org.example.model.dto.CourseSearchReq;
import org.example.model.dto.CourseStat;
import org.example.model.entity.Course;
import org.example.repository.CourseRepository;
import org.hibernate.Session;
import org.hibernate.jpa.spi.NativeQueryConstructorTransformer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseJpaRepository implements CourseRepository {
    private final JPAClient db;

    public CourseJpaRepository(JPAClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("from Course", Course.class).getResultList();
        }
    }

    @Override
    public List<Course> findAll(int page, int size) {
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("from Course c order by c.id", Course.class)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    @Override
    public Optional<Course> findById(Integer id) {
        try (EntityManager em = db.getEntityManager()) {
            return Optional.ofNullable(em.find(Course.class, id));
        }
    }

    @Override
    public List<CourseStat> getCourseStats(int opt) {
        return switch (opt) {
            // Opt 1: JPQL + JPA Tuple
            case 1 -> getCourseStatsUsingJpqlAndTuple();
            // Opt 2: JPQL + Constructor Expression
            case 2 -> getCourseStatsUsingJpqlAndConstructorExpression();
            // Opt 3: Native SQL + JPA Tuple
            case 3 -> getCourseStatsUsingNativeQueryAndTuple();
            // Opt 4: @NamedNativeQuery + @SqlResultSetMapping
            case 4 -> getCourseStatsUsingNativeQueryAndMappingAnnotations();
            // Opt 5: (Hibernate APIs) Native SQL + TupleTransformer
            case 5 -> getCourseStatsUsingNativeQueryAndTupleTransformer();
            default -> List.of();
        };
    }

    @Override
    public Course save(Course course) {
        try (EntityManager em = db.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(course);
            em.getTransaction().commit();
            return course;
        }
    }

    @Override
    public Course update(Course course) {
        try (EntityManager em = db.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(course);
            em.getTransaction().commit();
            return course;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = db.getEntityManager()) {
            em.getTransaction().begin();
            Course course = em.find(Course.class, id);
            em.remove(course);
            em.getTransaction().commit();
        }
    }

    @Override
    public List<Course> findByCriteria(CourseSearchReq criteria) {
        return List.of();
    }

    private List<CourseStat> getCourseStatsUsingJpqlAndTuple() {
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("""
                            select
                                c.id courseId,
                                c.name courseName,
                                count(e.student.id) studentCount
                            from Course c
                            left join c.enrollments e
                            group by c.id, c.name
                            """, Tuple.class)
                    .getResultStream()
                    .map(tuple -> {
                        CourseStat courseStat = new CourseStat();
                        courseStat.setCourseId(tuple.get("courseId", Integer.class));
                        courseStat.setCourseName(tuple.get("courseName", String.class));
                        courseStat.setStudentCount(tuple.get("studentCount", Long.class));
                        return courseStat;
                    })
                    .toList();
        }
    }

    private List<CourseStat> getCourseStatsUsingJpqlAndConstructorExpression() {
        // count(e.student.id) returns Long -> DTO depends on JPQL
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("""
                            select new org.example.model.dto.CourseStat(
                                c.id,
                                c.name,
                                count(e.student.id)
                            )
                            from Course c
                            left join c.enrollments e
                            group by c.id, c.name
                            """, CourseStat.class)
                    .getResultList();
        }
    }

    private List<CourseStat> getCourseStatsUsingNativeQueryAndTuple() {
        try (EntityManager em = db.getEntityManager()) {
            Query query = em.createNativeQuery("""
                    SELECT
                        c.course_id courseId,
                        c.name courseName,
                        CAST(COUNT(e.student_id) AS BIGINT) studentCount
                    FROM course c
                    LEFT JOIN enrollment e ON c.course_id = e.course_id
                    GROUP BY c.course_id, c.name
                    """, Tuple.class);
            List<Tuple> rs = query.getResultList();
            return rs.stream()
                    .map(tuple -> {
                        CourseStat courseStat = new CourseStat();
                        courseStat.setCourseId(tuple.get("courseId", Integer.class));
                        courseStat.setCourseName(tuple.get("courseName", String.class));
                        courseStat.setStudentCount(tuple.get("studentCount", Long.class));
                        return courseStat;
                    })
                    .toList();
        }
    }

    private List<CourseStat> getCourseStatsUsingNativeQueryAndMappingAnnotations() {
        try (EntityManager em = db.getEntityManager()) {
            return em.createNamedQuery("getCourseStats", CourseStat.class).getResultList();
        }
    }

    private List<CourseStat> getCourseStatsUsingNativeQueryAndTupleTransformer() {
        try (EntityManager em = db.getEntityManager()) {
            return em.unwrap(Session.class)
                    .createNativeQuery("""
                            SELECT
                                c.course_id courseId,
                                c.name courseName,
                                CAST(COUNT(e.student_id) AS BIGINT) studentCount
                            FROM course c
                            LEFT JOIN enrollment e ON c.course_id = e.course_id
                            GROUP BY c.course_id, c.name
                            """, Object[].class)
                    .setTupleTransformer(new NativeQueryConstructorTransformer<>(CourseStat.class))
                    .getResultList();
        }
    }
}

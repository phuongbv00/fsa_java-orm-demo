package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.example.config.db.JPAClient;
import org.example.model.Course;
import org.example.model.dto.CourseStat;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<CourseStat> getCourseStats(int opt) {
        return switch (opt) {
            // Opt 1: JPQL + JPA Tuple
            case 1 -> getCourseStatsUsingJpqlAndTuple();
            // Opt 2: JPQL + Constructor Expression
            case 2 -> getCourseStatsUsingJpqlAndConstructorExpression();
            // Opt 3: Native SQL + JPA Tuple
            case 3 -> getCourseStatsUsingNativeQueryAndTuple();
            // Opt 4: @NamedNativeQuery + @SqlResultSetMapping
            case 4 -> List.of();
            default -> List.of();
        };
    }

    private List<CourseStat> getCourseStatsUsingJpqlAndTuple() {
        try (EntityManager em = db.getEntityManager()) {
            TypedQuery<Tuple> query = em.createQuery("""
                    select
                        c.id courseId,
                        c.name courseName,
                        count(e.student.id) studentCount
                    from Course c
                    left join Enrollment e on e.course.id = c.id
                    group by c.id, c.name
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

    private List<CourseStat> getCourseStatsUsingJpqlAndConstructorExpression() {
        // count(e.student.id) returns Long -> DTO is depend on JPQL
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("""
                            select new org.example.model.dto.CourseStat(
                                c.id,
                                c.name,
                                count(e.student.id)
                            )
                            from Course c
                            left join Enrollment e on e.course.id = c.id
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
                        COUNT(e.student_id) studentCount
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
                        // COUNT(e.student_id) returns int -> casting
                        courseStat.setStudentCount(tuple.get("studentCount", Integer.class).longValue());
                        return courseStat;
                    })
                    .toList();
        }
    }
}

package fsa.java.orm.demo.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import fsa.java.orm.demo.config.db.JPAClient;
import fsa.java.orm.demo.model.dto.CourseSearchReq;
import fsa.java.orm.demo.model.dto.CourseStat;
import fsa.java.orm.demo.model.entity.Course;
import fsa.java.orm.demo.model.entity.Enrollment;
import fsa.java.orm.demo.model.entity.Instructor;
import fsa.java.orm.demo.model.entity.Student;
import fsa.java.orm.demo.repository.CourseRepository;
import org.hibernate.Session;
import org.hibernate.jpa.spi.NativeQueryConstructorTransformer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            // Opt 6: Criteria API
            case 6 -> getCourseStatsUsingCriteriaQuery();
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
        try (EntityManager em = db.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Course> cq = cb.createQuery(Course.class);
            Root<Course> root = cq.from(Course.class);
            Join<Course, Instructor> joinInstructor = root.join("instructor");
            if (criteria != null) {
                List<Predicate> predicates = new ArrayList<>();
                if (criteria.name() != null) {
                    predicates.add(cb.like(root.get("name"), criteria.name() + "%"));
                }
                if (criteria.minCapacity() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), criteria.minCapacity()));
                }
                if (criteria.maxCapacity() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), criteria.maxCapacity()));
                }
                if (criteria.minStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), criteria.minStartDate()));
                }
                if (criteria.maxEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), criteria.maxEndDate()));
                }
                if (criteria.instructorId() != null) {
                    predicates.add(cb.equal(joinInstructor.get("id"), criteria.instructorId()));
                }
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            TypedQuery<Course> query = em.createQuery(cq.select(root));
            return query.getResultList();
        }
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

    private List<CourseStat> getCourseStatsUsingCriteriaQuery() {
        try (EntityManager em = db.getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CourseStat> cq = cb.createQuery(CourseStat.class);
            Root<Course> root = cq.from(Course.class);
            Join<Course, Enrollment> joinEnrollment = root.join("enrollments", JoinType.LEFT);
            Join<Enrollment, Student> joinStudent = joinEnrollment.join("student", JoinType.INNER);
            cq.multiselect(
                    root.get("id").alias("courseId"),
                    root.get("name").alias("courseName"),
                    cb.count(joinStudent.get("id")).alias("studentCount")
            ).groupBy(root.get("id"), root.get("name"));

            return em.createQuery(cq).getResultList();
        }
    }
}

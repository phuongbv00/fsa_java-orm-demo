package fsa.java.orm.demo.repository.impl;

import jakarta.persistence.TypedQuery;
import fsa.java.orm.demo.config.db.HibernateClient;
import fsa.java.orm.demo.model.dto.CourseSearchReq;
import fsa.java.orm.demo.model.dto.CourseStat;
import fsa.java.orm.demo.model.entity.Course;
import fsa.java.orm.demo.repository.CourseRepository;
import org.hibernate.Session;
import org.hibernate.jpa.spi.NativeQueryConstructorTransformer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseHibernateRepository implements CourseRepository {
    private final HibernateClient db;

    public CourseHibernateRepository(HibernateClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (Session ss = db.getSession()) {
            return ss.createQuery("from Course", Course.class).getResultList();
        }
    }

    @Override
    public List<Course> findAll(int page, int size) {
        try (Session ss = db.getSession()) {
            return ss.createQuery("from Course c order by c.id", Course.class)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    @Override
    public Optional<Course> findById(Integer id) {
        try (Session ss = db.getSession()) {
            return Optional.ofNullable(ss.find(Course.class, id));
        }
    }

    @Override
    public List<CourseStat> getCourseStats(int opt) {
        return getCourseStatsUsingNativeQueryAndTupleTransformer();
    }

    @Override
    public Course save(Course course) {
        try (Session ss = db.getSession()) {
            ss.getTransaction().begin();
            ss.persist(course);
            ss.getTransaction().commit();
            return course;
        }
    }

    @Override
    public Course update(Course course) {
        try (Session ss = db.getSession()) {
            ss.getTransaction().begin();
            ss.merge(course);
            ss.getTransaction().commit();
            return course;
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session ss = db.getSession()) {
            Course course = new Course();
            course.setId(id);
            ss.getTransaction().begin();
            ss.remove(course);
            ss.getTransaction().commit();
        }
    }

    @Override
    public List<Course> findByCriteria(CourseSearchReq criteria) {
        try (Session ss = db.getSession()) {
            StringBuilder queryBuilder = new StringBuilder("from Course c where 1=1");
            Map<String, Object> params = new HashMap<>();
            if (criteria != null) {
                if (criteria.name() != null) {
                    queryBuilder.append("\n\tand c.name like :name");
                    params.put("name", criteria.name() + "%");
                }
                if (criteria.minCapacity() != null) {
                    queryBuilder.append("\n\tand c.capacity >= :minCapacity");
                    params.put("minCapacity", criteria.minCapacity());
                }
                if (criteria.maxCapacity() != null) {
                    queryBuilder.append("\n\tand c.capacity <= :maxCapacity");
                    params.put("maxCapacity", criteria.maxCapacity());
                }
                if (criteria.minStartDate() != null) {
                    queryBuilder.append("\n\tand c.startDate >= :minStartDate");
                    params.put("minStartDate", criteria.minStartDate());
                }
                if (criteria.maxEndDate() != null) {
                    queryBuilder.append("\n\tand c.endDate <= :maxEndDate");
                    params.put("maxEndDate", criteria.maxEndDate());
                }
                if (criteria.instructorId() != null) {
                    queryBuilder.append("\n\tand c.instructor.id = :instructorId");
                    params.put("instructorId", criteria.instructorId());
                }
            }
            TypedQuery<Course> query = ss.createQuery(queryBuilder.toString(), Course.class);
            params.forEach(query::setParameter);
            return query.getResultList();
        }
    }

    private List<CourseStat> getCourseStatsUsingNativeQueryAndTupleTransformer() {
        try (Session ss = db.getSession()) {
            return ss.createNativeQuery("""
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

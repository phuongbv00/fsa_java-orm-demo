package org.example.repository.impl;

import jakarta.persistence.EntityManager;
import org.example.config.db.HibernateClient;
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
            ss.detach(course);
            return course;
        }
    }

    @Override
    public Course update(Course course) {
        try (Session ss = db.getSession()) {
            ss.getTransaction().begin();
            ss.merge(course);
            ss.getTransaction().commit();
            ss.detach(course);
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
        return List.of();
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

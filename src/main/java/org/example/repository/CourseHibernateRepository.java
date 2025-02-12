package org.example.repository;

import org.example.config.db.HibernateClient;
import org.example.model.Course;
import org.example.model.dto.CourseStat;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<CourseStat> getCourseStats(int opt) {
        return List.of();
    }
}

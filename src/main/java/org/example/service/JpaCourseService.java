package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.config.db.JPAClient;
import org.example.model.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaCourseService implements CourseService {
    private final JPAClient db;

    public JpaCourseService(JPAClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (EntityManager em = db.getEntityManager()) {
            return em.createQuery("from Course", Course.class).getResultList();
        }
    }
}

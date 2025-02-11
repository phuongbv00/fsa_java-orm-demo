package org.example.service;

import org.example.config.db.HibernateClient;
import org.example.model.Course;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateCourseService implements CourseService {
    private final HibernateClient db;

    public HibernateCourseService(HibernateClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (Session ss = db.getSession()) {
            return ss.createQuery("from Course", Course.class).getResultList();
        }
    }
}

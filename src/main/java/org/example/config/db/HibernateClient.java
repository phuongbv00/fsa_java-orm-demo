package org.example.config.db;

import org.example.model.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

@Component
public class HibernateClient {
    private final SessionFactory sessionFactory;

    public HibernateClient() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Course.class)
                .buildSessionFactory();
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}

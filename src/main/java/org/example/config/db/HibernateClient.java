package org.example.config.db;

import org.example.model.entity.Course;
import org.example.model.entity.Enrollment;
import org.example.model.entity.Instructor;
import org.example.model.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class HibernateClient {
    private final SessionFactory sessionFactory;

    public HibernateClient() {
        try (InputStream in = HibernateClient.class.getClassLoader().getResourceAsStream("hibernate.cfg.properties")) {
            Properties properties = new Properties();
            properties.load(in);

            sessionFactory = new Configuration()
                    .addProperties(properties)
                    .addAnnotatedClass(Course.class)
                    .addAnnotatedClass(Student.class)
                    .addAnnotatedClass(Instructor.class)
                    .addAnnotatedClass(Enrollment.class)
                    .buildSessionFactory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}

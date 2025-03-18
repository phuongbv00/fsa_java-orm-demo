package fsa.java.orm.demo.config.db;

import fsa.java.orm.demo.model.entity.Course;
import fsa.java.orm.demo.model.entity.Enrollment;
import fsa.java.orm.demo.model.entity.Instructor;
import fsa.java.orm.demo.model.entity.Student;
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

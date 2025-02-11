package org.example.service;

import org.example.config.db.JDBCClient;
import org.example.model.Course;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcCourseService implements CourseService {
    private final JDBCClient db;

    public JdbcCourseService(JDBCClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("select * from course").executeQuery();
            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("course_id"));
                course.setName(rs.getString("name"));
                course.setDuration(rs.getInt("duration"));
                courses.add(course);
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.example.repository.impl;

import org.example.config.db.JDBCClient;
import org.example.model.dto.CourseStat;
import org.example.model.entity.Course;
import org.example.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseJdbcRepository implements CourseRepository {
    private final JDBCClient db;

    public CourseJdbcRepository(JDBCClient db) {
        this.db = db;
    }

    @Override
    public List<Course> findAll() {
        try (Connection conn = db.getConnection()) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM course").executeQuery();
            return fetchCoursesResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> findAll(int page, int size) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_get_paginated_courses(?, ?)");
            stmt.setInt(1, page);
            stmt.setInt(2, size);
            ResultSet rs = stmt.executeQuery();
            return fetchCoursesResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CourseStat> getCourseStats(int opt) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_get_course_stats()");
            ResultSet rs = stmt.executeQuery();
            return fetchCourseStatsResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Course course) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("""
                    INSERT INTO course(name, capacity, start_date, end_date, instructor_id)
                    VALUES (?, ?, ?, ?, ?)
                    """);
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getCapacity());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getStartDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(course.getEndDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setInt(5, course.getInstructor().getId());
            stmt.execute();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Course> fetchCoursesResultSet(ResultSet rs) throws SQLException {
        List<Course> courses = new ArrayList<>();
        while (rs.next()) {
            Course course = new Course();
            course.setId(rs.getInt("course_id"));
            course.setName(rs.getString("name"));
            course.setCapacity(rs.getInt("capacity"));
            courses.add(course);
        }
        return courses;
    }

    private List<CourseStat> fetchCourseStatsResultSet(ResultSet rs) throws SQLException {
        List<CourseStat> stats = new ArrayList<>();
        while (rs.next()) {
            CourseStat stat = new CourseStat();
            stat.setCourseId(rs.getInt("course_id"));
            stat.setCourseName(rs.getString("name"));
            stat.setStudentCount(rs.getLong("std_count"));
            stats.add(stat);
        }
        return stats;
    }
}

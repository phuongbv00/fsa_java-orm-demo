package org.example.repository;

import org.example.config.db.JDBCClient;
import org.example.model.Course;
import org.example.model.dto.CourseStat;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<CourseStat> getCourseStats() {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_get_course_stats()");
            ResultSet rs = stmt.executeQuery();
            return fetchCourseStatsResultSet(rs);
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
            course.setDuration(rs.getInt("duration"));
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

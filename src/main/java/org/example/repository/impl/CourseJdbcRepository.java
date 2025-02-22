package org.example.repository.impl;

import org.example.config.db.JDBCClient;
import org.example.model.dto.CourseSearchReq;
import org.example.model.dto.CourseStat;
import org.example.model.entity.Course;
import org.example.model.entity.Instructor;
import org.example.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<Course> findById(Integer id) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM course WHERE course_id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return fetchCoursesResultSet(rs).stream().findFirst();
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
    public Course save(Course course) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("""
                    INSERT INTO course(name, capacity, start_date, end_date, instructor_id)
                    VALUES (?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getCapacity());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getStartDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(course.getEndDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setInt(5, course.getInstructor().getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    course.setId(id);
                }
            }
            conn.commit();
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course update(Course course) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("""
                    UPDATE course
                    SET name = ?, capacity = ?, start_date = ?, end_date = ?, instructor_id = ?
                    WHERE course_id = ?
                    """);
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getCapacity());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getStartDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(course.getEndDate().atOffset(ZoneOffset.UTC).toLocalDateTime()));
            stmt.setInt(5, course.getInstructor().getId());
            stmt.setInt(6, course.getId());
            stmt.executeUpdate();
            conn.commit();
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("""
                    DELETE course WHERE course_id = ?
                    """, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> findByCriteria(CourseSearchReq criteria) {
        try (Connection conn = db.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                    SELECT *
                    FROM course
                    WHERE (name LIKE ?)
                        AND (capacity >= ?)
                        AND (capacity <= ?)
                        AND (course.start_date >= ?)
                        AND (course.end_date <= ?)
                        AND (course.instructor_id = ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, criteria.name() + "%");
            stmt.setInt(2, criteria.minCapacity());
            stmt.setInt(3, criteria.maxCapacity());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.ofInstant(criteria.minStartDate(), ZoneOffset.UTC)));
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.ofInstant(criteria.maxEndDate(), ZoneOffset.UTC)));
            stmt.setInt(6, criteria.instructorId());
            ResultSet rs = stmt.executeQuery();
            return fetchCoursesResultSet(rs);
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
            course.setStartDate(Optional.ofNullable(rs.getTimestamp("start_date")).map(Timestamp::toInstant).orElse(null));
            course.setEndDate(Optional.ofNullable(rs.getTimestamp("end_date")).map(Timestamp::toInstant).orElse(null));
            Instructor instructor = new Instructor();
            instructor.setId(rs.getInt("instructor_id"));
            course.setInstructor(instructor);
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

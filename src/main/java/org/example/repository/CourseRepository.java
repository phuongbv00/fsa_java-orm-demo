package org.example.repository;

import org.example.model.dto.CourseSearchReq;
import org.example.model.dto.CourseStat;
import org.example.model.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    List<Course> findAll();

    List<Course> findAll(int page, int size);

    Optional<Course> findById(Integer id);

    List<CourseStat> getCourseStats(int opt);

    Course save(Course course);

    Course update(Course course);

    void delete(Integer id);

    List<Course> findByCriteria(CourseSearchReq criteria);
}

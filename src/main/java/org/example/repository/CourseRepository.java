package org.example.repository;

import org.example.model.entity.Course;
import org.example.model.dto.CourseStat;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll();

    List<Course> findAll(int page, int size);

    List<CourseStat> getCourseStats(int opt);

    void save(Course course);
}

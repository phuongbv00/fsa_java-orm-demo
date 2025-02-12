package org.example.repository;

import org.example.model.Course;
import org.example.model.dto.CourseStat;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll();

    List<Course> findAll(int page, int size);

    List<CourseStat> getCourseStats();
}

package org.example.repository;

import org.example.model.dto.CourseSearchReq;
import org.example.model.dto.CourseStat;
import org.example.model.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<CourseStat> getCourseStats(int opt);

    List<Course> findByCriteria(CourseSearchReq criteria);
}

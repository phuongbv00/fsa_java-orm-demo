package fsa.java.orm.demo.repository;

import fsa.java.orm.demo.model.dto.CourseSearchReq;
import fsa.java.orm.demo.model.dto.CourseStat;
import fsa.java.orm.demo.model.entity.Course;

import java.util.List;

public interface CourseRepository extends MyJpaRepository<Course, Integer> {
    List<CourseStat> getCourseStats(int opt);

    List<Course> findByCriteria(CourseSearchReq criteria);
}

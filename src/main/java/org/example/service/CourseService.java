package org.example.service;

import org.example.model.dto.CourseDTO;
import org.example.model.dto.CourseWithInstructorDTO;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<CourseDTO> findAll();

    Optional<CourseDTO> findById(Integer id);

    void save(CourseDTO course);

    void update(CourseDTO course);

    void delete(Integer id);

    List<CourseWithInstructorDTO> findAllWithInstructor();
}

package org.example.service.impl;

import org.example.model.dto.CourseDTO;
import org.example.model.dto.CourseWithInstructorDTO;
import org.example.model.dto.InstructorDTO;
import org.example.model.entity.Course;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.service.CourseService;
import org.example.util.FormatUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public CourseServiceImpl(@Qualifier("courseJpaRepository") CourseRepository courseRepository, InstructorRepository instructorRepository) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    @Override
    public List<CourseDTO> findAll() {
        return courseRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public Optional<CourseDTO> findById(Integer id) {
        return courseRepository.findById(id).map(this::toDTO);
    }

    @Override
    public void save(CourseDTO dto) {
        Course course = toEntity(dto);
        courseRepository.save(course);
    }

    @Override
    public void update(CourseDTO dto) {
        Course course = toEntity(dto);
        courseRepository.update(course);
    }

    @Override
    public void delete(Integer id) {
        courseRepository.delete(id);
    }

    @Override
    public List<CourseWithInstructorDTO> findAllWithInstructor() {
        return courseRepository.findAll().stream().map(this::toWithInstructorDTO).toList();
    }

    private CourseDTO toDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setName(course.getName());
        courseDTO.setCapacity(course.getCapacity());
        courseDTO.setStartDate(FormatUtil.instantToLocalDateString(course.getStartDate()));
        courseDTO.setEndDate(FormatUtil.instantToLocalDateString(course.getEndDate()));
        if (course.getInstructor() != null) {
            courseDTO.setInstructorId(course.getInstructor().getId());
        }
        return courseDTO;
    }

    private CourseWithInstructorDTO toWithInstructorDTO(Course course) {
        CourseWithInstructorDTO courseDTO = new CourseWithInstructorDTO();
        courseDTO.setId(course.getId());
        courseDTO.setName(course.getName());
        courseDTO.setCapacity(course.getCapacity());
        courseDTO.setStartDate(FormatUtil.instantToLocalDateString(course.getStartDate()));
        courseDTO.setEndDate(FormatUtil.instantToLocalDateString(course.getEndDate()));
        if (course.getInstructor() != null) {
            courseDTO.setInstructorId(course.getInstructor().getId());
            InstructorDTO instructorDTO = new InstructorDTO();
            instructorDTO.setId(course.getInstructor().getId());
            instructorDTO.setName(course.getInstructor().getName());
            courseDTO.setInstructor(instructorDTO);
        }
        return courseDTO;
    }

    private Course toEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setName(courseDTO.getName());
        course.setCapacity(courseDTO.getCapacity());
        course.setStartDate(FormatUtil.localDateStringToInstant(courseDTO.getStartDate()));
        course.setEndDate(FormatUtil.localDateStringToInstant(courseDTO.getEndDate()));
        if (courseDTO.getInstructorId() != null) {
            course.setInstructor(instructorRepository.findById(courseDTO.getInstructorId()).orElse(null));
        }
        return course;
    }
}

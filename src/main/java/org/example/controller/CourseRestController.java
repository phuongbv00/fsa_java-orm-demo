package org.example.controller;

import org.example.model.dto.CourseWithInstructorDTO;
import org.example.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {
    private final CourseService courseService;
    // Memory leak demo
    private final Map<String, Object> requests = new HashMap<>();
    private final Random random = new Random();

    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseWithInstructorDTO> list() {
        List<CourseWithInstructorDTO> data = courseService.findAllWithInstructor();
        requests.put(getRequestId(), data);
        return data;
    }

    private String getRequestId() {
        return Instant.now().toEpochMilli() + "_" + random.nextInt(1000);
    }
}

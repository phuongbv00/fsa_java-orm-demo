package org.example;

import jakarta.annotation.PostConstruct;
import org.example.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
//    @Qualifier("jdbcCourseService")
//    @Qualifier("hibernateCourseService")
    @Qualifier("jpaCourseService")
    CourseService courseService;

    @PostConstruct
    public void init() {
        System.out.println(courseService.findAll());
    }
}
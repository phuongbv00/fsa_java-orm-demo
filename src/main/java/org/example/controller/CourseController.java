package org.example.controller;

import org.example.model.dto.CourseDTO;
import org.example.repository.InstructorRepository;
import org.example.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    // TODO: replace by InstructorService
    private final InstructorRepository instructorRepository;

    public CourseController(CourseService courseService, InstructorRepository instructorRepository) {
        this.courseService = courseService;
        this.instructorRepository = instructorRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseService.findAllWithInstructor());
        return "courses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("instructors", instructorRepository.findAll());
        return "courses/form";
    }

    @PostMapping
    public String saveOrUpdate(@ModelAttribute("course") @Validated CourseDTO course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("instructors", instructorRepository.findAll());
            return "courses/form";
        }
        if (course.getId() == null) {
            courseService.save(course);
        } else {
            courseService.update(course);
        }
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<CourseDTO> course = courseService.findById(id);
        if (course.isPresent()) {
            model.addAttribute("course", course.get());
            model.addAttribute("instructors", instructorRepository.findAll());
            return "courses/form";
        }
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        courseService.delete(id);
        return "redirect:/courses";
    }
}


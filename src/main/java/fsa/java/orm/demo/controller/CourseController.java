package fsa.java.orm.demo.controller;

import jakarta.validation.Valid;
import fsa.java.orm.demo.model.dto.CourseDTO;
import fsa.java.orm.demo.repository.InstructorRepository;
import fsa.java.orm.demo.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("instructors", instructorRepository.findAll());
        return "courses/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("course") CourseDTO course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("instructors", instructorRepository.findAll());
            return "courses/add";
        }
        courseService.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<CourseDTO> course = courseService.findById(id);
        if (course.isPresent()) {
            model.addAttribute("course", course.get());
            model.addAttribute("instructors", instructorRepository.findAll());
            return "courses/edit";
        }
        return "redirect:/courses";
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid @ModelAttribute("course") CourseDTO course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("instructors", instructorRepository.findAll());
            return "courses/edit";
        }
        courseService.update(course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        courseService.delete(id);
        return "redirect:/courses";
    }
}


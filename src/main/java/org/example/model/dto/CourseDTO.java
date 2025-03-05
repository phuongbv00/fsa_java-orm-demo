package org.example.model.dto;

import jakarta.validation.constraints.Max;
import org.example.validation.constraint.ValidCourseName;

public class CourseDTO {
    private Integer id;
    @ValidCourseName
    private String name;
    @Max(value = 100, message = "Max capacity is 100")
    private Integer capacity;
    // TODO: Thymeleaf only handles LocalDate and String, does not accept Instant
    private String startDate;
    private String endDate;
    private Integer instructorId;

    public CourseDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }
}

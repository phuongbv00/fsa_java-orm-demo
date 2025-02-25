package org.example.model.dto;

public class CourseWithInstructorDTO extends CourseDTO {
    private InstructorDTO instructor;

    public InstructorDTO getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorDTO instructor) {
        this.instructor = instructor;
    }
}

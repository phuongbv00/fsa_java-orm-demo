package org.example.model.dto;

import java.util.Objects;

public class CourseStat {
    private Integer courseId;
    private String courseName;
    private Long studentCount;

    public CourseStat() {
    }

    public CourseStat(Integer courseId, String courseName, Long studentCount) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.studentCount = studentCount;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Long studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return "CourseStat{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseStat that = (CourseStat) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(courseName, that.courseName) && Objects.equals(studentCount, that.studentCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, courseName, studentCount);
    }
}

package org.example.model;

import jakarta.persistence.*;
import org.example.model.dto.CourseStat;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "course")
@NamedNativeQuery(
        name = "getCourseStats",
        query = """
                SELECT
                    c.course_id courseId,
                    c.name courseName,
                    CAST(COUNT(e.student_id) AS BIGINT) studentCount
                FROM course c
                LEFT JOIN enrollment e ON c.course_id = e.course_id
                GROUP BY c.course_id, c.name
                """,
        resultSetMapping = "getCourseStatsRsMapping"
)
@SqlResultSetMapping(
        name = "getCourseStatsRsMapping",
        classes = @ConstructorResult(
                targetClass = CourseStat.class,
                columns = {
                        @ColumnResult(name = "courseId"),
                        @ColumnResult(name = "courseName"),
                        @ColumnResult(name = "studentCount")
                }
        )
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;

    private String name;

    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", hash=" + hashCode() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name) && Objects.equals(duration, course.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, duration);
    }
}

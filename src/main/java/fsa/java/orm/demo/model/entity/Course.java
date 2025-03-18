package fsa.java.orm.demo.model.entity;

import jakarta.persistence.*;
import fsa.java.orm.demo.model.dto.CourseStat;

import java.time.Instant;
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

    @Column(unique = true)
    private String name;

    private Integer capacity;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @ManyToOne
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
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
                "endDate=" + endDate +
                ", startDate=" + startDate +
                ", capacity=" + capacity +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name) && Objects.equals(capacity, course.capacity) && Objects.equals(startDate, course.startDate) && Objects.equals(endDate, course.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity, startDate, endDate);
    }
}

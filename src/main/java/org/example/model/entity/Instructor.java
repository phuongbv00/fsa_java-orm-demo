package org.example.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "instructor")
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_id")
    private Integer id;

    private String name;

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

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Instructor instructor = (Instructor) o;
        return Objects.equals(id, instructor.id) && Objects.equals(name, instructor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

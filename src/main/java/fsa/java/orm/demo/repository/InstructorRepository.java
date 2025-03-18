package fsa.java.orm.demo.repository;

import fsa.java.orm.demo.model.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
}

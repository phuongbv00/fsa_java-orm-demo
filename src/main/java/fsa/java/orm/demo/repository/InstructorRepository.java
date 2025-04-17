package fsa.java.orm.demo.repository;

import fsa.java.orm.demo.model.dto.InstructorNameOnly;
import fsa.java.orm.demo.model.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    @NativeQuery("select name from instructor where instructor_id like :s% or name like :s%")
    List<InstructorNameOnly> search(String s);
}

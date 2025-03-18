package fsa.java.orm.demo.repository;

import fsa.java.orm.demo.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Procedure(procedureName = "usp_process_enrollments", outputParameterName = "num_processed")
    int processEnrollments(@Param("max_student_enrollment_count") int maxStudentEnrollmentCount);
}

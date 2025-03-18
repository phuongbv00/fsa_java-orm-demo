package fsa.java.orm.demo.model.dto;

import java.time.Instant;

public record CourseSearchReq(
        String name,
        Integer minCapacity,
        Integer maxCapacity,
        Instant minStartDate,
        Instant maxEndDate,
        Integer instructorId
) {
}

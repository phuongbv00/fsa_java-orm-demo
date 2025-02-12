DROP FUNCTION IF EXISTS func_get_course_stats;

CREATE FUNCTION func_get_course_stats()
    RETURNS TABLE
        AS RETURN
        SELECT crs.course_id, crs.name, COUNT(enrl.student_id) as std_count
        FROM course crs
                 LEFT JOIN enrollment enrl ON crs.course_id = enrl.course_id
        GROUP BY crs.course_id, crs.name

SELECT *
FROM func_get_course_stats();
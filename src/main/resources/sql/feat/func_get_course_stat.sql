DROP FUNCTION IF EXISTS FUNC_GET_COURSE_STAT;

CREATE FUNCTION FUNC_GET_COURSE_STAT()
    RETURNS TABLE
        AS RETURN
        SELECT crs.course_id, crs.name, COUNT(enrl.student_id) as std_count
        FROM course crs
                 JOIN enrollment enrl ON crs.course_id = enrl.course_id
        GROUP BY crs.course_id, crs.name

SELECT *
FROM FUNC_GET_COURSE_STAT();
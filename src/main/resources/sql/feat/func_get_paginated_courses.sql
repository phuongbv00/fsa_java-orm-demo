DROP FUNCTION IF EXISTS func_get_paginated_courses;

CREATE FUNCTION func_get_paginated_courses(@page INT, @size INT)
    RETURNS TABLE
        AS RETURN
        SELECT *
        FROM course
        ORDER BY course_id
        OFFSET (@page - 1) * @size ROWS FETCH NEXT @size ROWS ONLY;

SELECT *
FROM func_get_paginated_courses(2, 1);
begin
    declare @processed int;
    exec usp_process_enrollments
         @max_student_enrollment_count=1,
         @num_processed=@processed out;
    print 'processed: ' + convert(nvarchar(10), @processed)
end;

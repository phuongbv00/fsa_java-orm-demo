begin
    declare @processed int;
    declare @skipped int;
    declare @approved int;
    declare @rejected int;
    exec proc_process_enrollments
         @max_student_enrollment_count=1,
         @num_processed=@processed out,
         @num_skipped=@skipped out,
         @num_approved=@approved out,
         @num_rejected=@rejected out;
    print 'processed: ' + convert(nvarchar(10), @processed)
        + CHAR(13) + 'skipped: ' + convert(nvarchar(10), @skipped)
        + CHAR(13) + 'approved: ' + convert(nvarchar(10), @approved)
        + CHAR(13) + 'rejected: ' + convert(nvarchar(10), @rejected);
end;

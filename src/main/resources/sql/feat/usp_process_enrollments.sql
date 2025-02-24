drop procedure if exists usp_process_enrollments;

create procedure usp_process_enrollments
    @max_student_enrollment_count int,
    @num_processed int out,
    @num_skipped int out,
    @num_approved int out,
    @num_rejected int out
as
begin
    set nocount on;

    set @num_processed = 0;
    set @num_skipped = 0;
    set @num_approved = 0;
    set @num_rejected = 0;

    declare @enrollment_id int;
    declare @course_id int;
    declare @student_id int;

    declare cur cursor for
        select id, course_id, student_id
        from enrollment
        where status = 'PENDING'
        order by enrollment_date;

    open cur;

    fetch next from cur into @enrollment_id, @course_id, @student_id;

    while @@fetch_status = 0
        begin
            declare @course_start_dt datetimeoffset;
            declare @course_end_dt datetimeoffset;
            declare @course_capacity int;

            select @course_start_dt = start_date,
                   @course_end_dt = end_date,
                   @course_capacity = capacity
            from course
            where course_id = @course_id;

            if @course_start_dt is null or @course_end_dt is null
                begin
                    update enrollment
                    set comment = 'WAIT_FOR_COURSE_DATE_FINALIZATION'
                    where id = @enrollment_id;

                    set @num_skipped+=1;

                    fetch next from cur into @enrollment_id, @course_id, @student_id;
                    continue;
                end;

            if @course_start_dt < sysdatetimeoffset()
                begin
                    update enrollment
                    set status  = 'REJECTED',
                        comment = 'COURSE_ALREADY_STARTED'
                    where id = @enrollment_id;

                    set @num_rejected+=1;
                    set @num_processed+=1;

                    fetch next from cur into @enrollment_id, @course_id, @student_id;
                    continue;
                end;

            if @course_end_dt < sysdatetimeoffset()
                begin
                    update enrollment
                    set status  = 'REJECTED',
                        comment = 'COURSE_ALREADY_ENDED'
                    where id = @enrollment_id;

                    set @num_rejected+=1;
                    set @num_processed+=1;

                    fetch next from cur into @enrollment_id, @course_id, @student_id;
                    continue;
                end;

            declare @course_approved_student_count int;

            select @course_approved_student_count = count(student_id)
            from enrollment
            where course_id = @course_id
              and status = 'APPROVED';

            if @course_approved_student_count > @course_capacity
                begin
                    update enrollment
                    set status  = 'REJECTED',
                        comment = 'COURSE_GOT_MAX_CAPACITY'
                    where id = @enrollment_id;

                    set @num_rejected+=1;
                    set @num_processed+=1;

                    fetch next from cur into @enrollment_id, @course_id, @student_id;
                    continue;
                end;

            declare @student_enrollment_count int;

            select @student_enrollment_count = count(course_id)
            from enrollment
            where student_id = @student_id
              and status = 'APPROVED';

            if @student_enrollment_count >= @max_student_enrollment_count
                begin
                    update enrollment
                    set status  = 'REJECTED',
                        comment = 'STUDENT_GOT_MAX_NUMBER_OF_ENROLLMENTS'
                    where id = @enrollment_id;

                    set @num_rejected+=1;
                    set @num_processed+=1;

                    fetch next from cur into @enrollment_id, @course_id, @student_id;
                    continue;
                end;

            update enrollment
            set status = 'APPROVED'
            where id = @enrollment_id;

            set @num_approved+=1;
            set @num_processed+=1;

            fetch next from cur into @enrollment_id, @course_id, @student_id;
        end;

    close cur;
    deallocate cur;
end;

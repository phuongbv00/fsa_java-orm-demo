USE course_mgmt;
GO

INSERT INTO instructor (name)
VALUES (N'Nguyễn Văn A'),
       (N'Trần Thị B');

INSERT INTO course (name, capacity, instructor_id, start_date, end_date)
VALUES ('Java Core', 40, 1, '2025-02-01', '2025-03-01'),
       ('Spring Framework', 50, 2, '2025-03-01', '2025-04-01');

INSERT INTO student (name)
VALUES (N'Lê Minh C'),
       (N'Phạm Thị D');

INSERT INTO enrollment (student_id, course_id)
VALUES (1, 1),
       (2, 2);

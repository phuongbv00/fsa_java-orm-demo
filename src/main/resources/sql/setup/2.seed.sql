USE course_mgmt;
GO

-- Thêm giảng viên
INSERT INTO instructor (name)
VALUES ('Nguyễn Văn A'),
       ('Trần Thị B');

-- Thêm khóa học
INSERT INTO course (name, duration, instructor_id)
VALUES ('Java Core', 40, 1),
       ('Spring Framework', 50, 2);

-- Thêm học viên
INSERT INTO student (name)
VALUES ('Lê Minh C'),
       ('Phạm Thị D');

-- Đăng ký khóa học
INSERT INTO enrollment (student_id, course_id)
VALUES (1, 1),
       (2, 2);

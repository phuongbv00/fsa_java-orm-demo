USE course_mgmt;
GO

INSERT INTO instructor (name)
VALUES ('Nguyễn Văn A'),
       ('Trần Thị B');

INSERT INTO course (name, capacity, instructor_id)
VALUES ('Java Core', 40, 1),
       ('Spring Framework', 50, 2);

INSERT INTO student (name)
VALUES ('Lê Minh C'),
       ('Phạm Thị D');

INSERT INTO enrollment (student_id, course_id)
VALUES (1, 1),
       (2, 2);

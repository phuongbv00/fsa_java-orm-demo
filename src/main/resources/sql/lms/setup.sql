CREATE DATABASE lms;
GO

USE lms;
GO

CREATE TABLE student
(
    student_id    INT IDENTITY (1,1) PRIMARY KEY,
    full_name     NVARCHAR(100) NOT NULL,
    date_of_birth DATE,
    email         NVARCHAR(100) UNIQUE,
    created_at    DATETIME DEFAULT GETDATE()
);

CREATE TABLE subject
(
    subject_id   INT IDENTITY (1,1) PRIMARY KEY,
    subject_name NVARCHAR(100) NOT NULL,
    credit       INT           NOT NULL CHECK (credit > 0),
    created_at   DATETIME DEFAULT GETDATE()
);

CREATE TABLE mark
(
    mark_id    INT IDENTITY (1,1) PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    score      DECIMAL(5, 2) CHECK (score >= 0 AND score <= 100),
    exam_date  DATE,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subject (subject_id) ON DELETE CASCADE
);

CREATE TABLE leaderboard
(
    rank_id       INT IDENTITY (1,1) PRIMARY KEY,
    student_id    INT NOT NULL,
    subject_id    INT NOT NULL,
    score         DECIMAL(5, 2),
    rank_position INT,
    updated_at    DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subject (subject_id) ON DELETE CASCADE
);

INSERT INTO student (full_name, date_of_birth, email)
VALUES ('Nguyen Van A', '2000-01-15', 'a1@example.com'),
       ('Tran Thi B', '2001-02-20', 'b2@example.com'),
       ('Le Van C', '1999-03-10', 'c3@example.com'),
       ('Pham Thi D', '2002-04-25', 'd4@example.com'),
       ('Hoang Van E', '2000-05-30', 'e5@example.com'),
       ('Bui Thi F', '2001-06-12', 'f6@example.com'),
       ('Do Van G', '1998-07-14', 'g7@example.com'),
       ('Vo Thi H', '1999-08-19', 'h8@example.com'),
       ('Ngo Van I', '2000-09-05', 'i9@example.com'),
       ('Dang Thi J', '2002-10-22', 'j10@example.com'),
       ('Nguyen Van K', '2000-11-11', 'k11@example.com'),
       ('Tran Thi L', '2001-12-01', 'l12@example.com'),
       ('Le Van M', '1998-02-14', 'm13@example.com'),
       ('Pham Thi N', '1999-03-29', 'n14@example.com'),
       ('Hoang Van O', '2002-04-17', 'o15@example.com'),
       ('Bui Thi P', '2000-05-23', 'p16@example.com'),
       ('Do Van Q', '2001-06-09', 'q17@example.com'),
       ('Vo Thi R', '1998-07-27', 'r18@example.com'),
       ('Ngo Van S', '1999-08-03', 's19@example.com'),
       ('Dang Thi T', '2000-09-15', 't20@example.com');

-- Thêm dữ liệu vào bảng subject
INSERT INTO subject (subject_name, credit)
VALUES ('Mathematics', 3),
       ('Physics', 4),
       ('Computer Science', 3);

-- Thêm dữ liệu vào bảng mark
INSERT INTO mark (student_id, subject_id, score, exam_date)
VALUES (1, 1, 85, '2024-02-01'),
       (1, 2, 78, '2024-02-01'),
       (1, 3, 90, '2024-02-01'),
       (2, 1, 76, '2024-02-02'),
       (2, 2, 88, '2024-02-02'),
       (2, 3, 92, '2024-02-02'),
       (3, 1, 90, '2024-02-03'),
       (3, 2, 85, '2024-02-03'),
       (3, 3, 80, '2024-02-03'),
       (4, 1, 70, '2024-02-04'),
       (4, 2, 75, '2024-02-04'),
       (4, 3, 85, '2024-02-04'),
       (5, 1, 88, '2024-02-05'),
       (5, 2, 92, '2024-02-05'),
       (5, 3, 95, '2024-02-05'),
       (6, 1, 60, '2024-02-06'),
       (6, 2, 68, '2024-02-06'),
       (6, 3, 74, '2024-02-06'),
       (7, 1, 85, '2024-02-07'),
       (7, 2, 90, '2024-02-07'),
       (7, 3, 88, '2024-02-07'),
       (8, 1, 79, '2024-02-08'),
       (8, 2, 82, '2024-02-08'),
       (8, 3, 91, '2024-02-08'),
       (9, 1, 73, '2024-02-09'),
       (9, 2, 77, '2024-02-09'),
       (9, 3, 89, '2024-02-09'),
       (10, 1, 91, '2024-02-10'),
       (10, 2, 94, '2024-02-10'),
       (10, 3, 97, '2024-02-10'),
       (11, 1, 67, '2024-02-11'),
       (11, 2, 72, '2024-02-11'),
       (11, 3, 81, '2024-02-11'),
       (12, 1, 83, '2024-02-12'),
       (12, 2, 87, '2024-02-12'),
       (12, 3, 92, '2024-02-12'),
       (13, 1, 78, '2024-02-13'),
       (13, 2, 81, '2024-02-13'),
       (13, 3, 88, '2024-02-13'),
       (14, 1, 71, '2024-02-14'),
       (14, 2, 74, '2024-02-14'),
       (14, 3, 83, '2024-02-14'),
       (15, 1, 89, '2024-02-15'),
       (15, 2, 93, '2024-02-15'),
       (15, 3, 96, '2024-02-15'),
       (16, 1, 62, '2024-02-16'),
       (16, 2, 66, '2024-02-16'),
       (16, 3, 71, '2024-02-16'),
       (17, 1, 80, '2024-02-17'),
       (17, 2, 84, '2024-02-17'),
       (17, 3, 89, '2024-02-17'),
       (18, 1, 75, '2024-02-18'),
       (18, 2, 78, '2024-02-18'),
       (18, 3, 85, '2024-02-18'),
       (19, 1, 69, '2024-02-19'),
       (19, 2, 73, '2024-02-19'),
       (19, 3, 80, '2024-02-19'),
       (20, 1, 92, '2024-02-20'),
       (20, 2, 95, '2024-02-20'),
       (20, 3, 98, '2024-02-20');
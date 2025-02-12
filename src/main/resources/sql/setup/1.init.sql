DROP DATABASE IF EXISTS course_mgmt;
GO

CREATE
    DATABASE course_mgmt;
GO

USE course_mgmt;
GO

CREATE TABLE instructor
(
    instructor_id INT IDENTITY (1,1) PRIMARY KEY,
    name          NVARCHAR(255) NOT NULL
);

CREATE TABLE course
(
    course_id     INT IDENTITY (1,1) PRIMARY KEY,
    name          NVARCHAR(255) NOT NULL UNIQUE,
    duration      INT CHECK (duration > 0),
    instructor_id INT           NULL,
    FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id) ON DELETE SET NULL
);

CREATE TABLE student
(
    student_id INT IDENTITY (1,1) PRIMARY KEY,
    name       NVARCHAR(255) NOT NULL
);

CREATE TABLE enrollment
(
    id              INT IDENTITY (1,1) PRIMARY KEY,
    student_id      INT NOT NULL,
    course_id       INT NOT NULL,
    enrollment_date DATETIMEOFFSET DEFAULT GETUTCDATE(),
    UNIQUE (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE
);

CREATE INDEX idx_course_instructor ON course (instructor_id);
CREATE INDEX idx_enrollment_student ON enrollment (student_id);
CREATE INDEX idx_enrollment_course ON enrollment (course_id);

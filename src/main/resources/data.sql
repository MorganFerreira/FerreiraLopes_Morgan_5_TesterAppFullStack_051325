INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');


INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES  ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq'),
        ('test1', 'test1', false, 'test1@gmail.com', '$2a$10$RmOq4Uy9p9r8984PHQpEZ.l83gg4NZ4TBCjTKzzcKKulrqqaDSDBG'),
        ('test2', 'test2', false, 'test2@gmail.com', '$2a$10$dQWSwY8cJtVdDufcGhj5Pe/mc9x.yqq3fZ4CfDu0CkhM0J7IwisXm');

INSERT INTO SESSIONS (name, description, teacher_id, date)
VALUES  ('test1 session', 'test1 session', 1, '2025-05-19 00:00:00'),
        ('test2 session', 'test2 session', 2, '2025-05-19 00:00:00'),
        ('test3 session', 'test3 session', 2, '2025-05-19 00:00:00'),
        ('test4 session', 'test4 session', 2, '2025-05-19 00:00:00');
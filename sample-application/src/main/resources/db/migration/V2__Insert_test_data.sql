-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

-- Insert users
INSERT INTO users_tbl (full_name, username, password, enabled, locked, credentials_expired)
VALUES
    ('Alice Johnson', 'alice', '$2a$10$Q6loYpyXg.yuOLoTCusaJuc1KfJCarAu.xoShSRXX2Vl2CNDbaYcy', TRUE, FALSE, FALSE),
    ('Bob Smith', 'bob', '$2a$10$Q6loYpyXg.yuOLoTCusaJuc1KfJCarAu.xoShSRXX2Vl2CNDbaYcy', TRUE, FALSE, FALSE);

-- Link users to roles
INSERT INTO user_roles (users_id, roles_id) VALUES
                    (1, 1),  -- Alice -> Admin
                    (2, 2);  -- Bob -> User
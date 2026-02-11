-- Users table
CREATE TABLE users_tbl (
   id INT AUTO_INCREMENT PRIMARY KEY,
   full_name VARCHAR(255) NOT NULL,
   username VARCHAR(255) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   enabled BOOLEAN DEFAULT FALSE,
   locked BOOLEAN DEFAULT FALSE,
   credentials_expired BOOLEAN DEFAULT FALSE
);

-- Roles table
CREATE TABLE roles (
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255)
);

-- Join table for many-to-many relationship between users and roles
CREATE TABLE user_roles (
    users_id INT NOT NULL,
    roles_id INT NOT NULL,
    PRIMARY KEY (users_id, roles_id),
    CONSTRAINT fk_user
        FOREIGN KEY (users_id) REFERENCES users_tbl(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_role
        FOREIGN KEY (roles_id) REFERENCES roles(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
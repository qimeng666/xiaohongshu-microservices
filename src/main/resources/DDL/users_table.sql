DROP TABLE IF EXISTS users;
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       registration_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_login_time   DATETIME     NULL
);
CREATE INDEX idx_email
    ON users(email);
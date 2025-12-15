CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_guest BOOLEAN NOT NULL DEFAULT FALSE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    pwd_hash TEXT NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    gender VARCHAR(20) DEFAULT NULL,
    birthday DATE DEFAULT NULL,
    is_whitelisted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_email CHECK (email LIKE '%@%'),
    CONSTRAINT chk_first_name CHECK (CHAR_LENGTH(first_name) >= 1),
    CONSTRAINT chk_last_name CHECK (CHAR_LENGTH(last_name) >= 1),
    CONSTRAINT chk_pwd_hash CHECK (CHAR_LENGTH(pwd_hash) >= 60),
    CONSTRAINT chk_language CHECK (CHAR_LENGTH(language) = 2),
    CONSTRAINT chk_gender CHECK (gender IN ('male', 'female', 'other', 'unspecified')),
    CONSTRAINT chk_birthday CHECK (birthday <= CURRENT_DATE)
    );
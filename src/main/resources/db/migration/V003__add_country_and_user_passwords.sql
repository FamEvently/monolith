CREATE TABLE IF NOT EXISTS user_passwords (
    user_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    
    CONSTRAINT pk_user_passwords
        PRIMARY KEY (user_id),
    
    CONSTRAINT fk_user_passwords_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,
    
    CONSTRAINT chk_password_hash_length 
        CHECK (CHAR_LENGTH(password_hash) >= 8
    )
);


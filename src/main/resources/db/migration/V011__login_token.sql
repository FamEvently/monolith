CREATE TABLE IF NOT EXISTS login_token (
    user_id BIGINT NOT NULL,
    login_token VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_login_token PRIMARY KEY (login_token),
    
    CONSTRAINT fk_login_token_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);

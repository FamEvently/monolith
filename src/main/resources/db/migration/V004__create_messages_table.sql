CREATE TABLE IF NOT EXISTS message (
    message_id CHAR(36) NOT NULL DEFAULT (UUID()),
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    image_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_messages
        PRIMARY KEY (message_id),
    
    CONSTRAINT fk_messages_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_messages_event
        FOREIGN KEY (event_id)
            REFERENCES event(event_id)
);


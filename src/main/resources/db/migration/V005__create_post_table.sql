-- Create post table
CREATE TABLE IF NOT EXISTS post (
    post_id CHAR(36) NOT NULL,
    message_id CHAR(36) NOT NULL UNIQUE,
    parent_post_id CHAR(36) NULL,
    
    CONSTRAINT pk_post
        PRIMARY KEY (post_id),
    
    CONSTRAINT fk_post_message
        FOREIGN KEY (message_id)
            REFERENCES message(message_id)
            ON DELETE CASCADE,
    
    CONSTRAINT fk_post_parent
        FOREIGN KEY (parent_post_id)
            REFERENCES post(post_id)
            ON DELETE SET NULL
);


CREATE TABLE user_follow (
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_user_follow
        PRIMARY KEY (follower_id, followed_id),
    
    CONSTRAINT fk_user_follow_follower
        FOREIGN KEY (follower_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,
    
    CONSTRAINT fk_user_follow_followed
        FOREIGN KEY (followed_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,
    
    CONSTRAINT chk_no_self_follow
        CHECK (follower_id != followed_id)
);

CREATE INDEX idx_user_follow_follower ON user_follow(follower_id);
CREATE INDEX idx_user_follow_followed ON user_follow(followed_id);

CREATE TABLE user_follow_stats (
    user_id BIGINT NOT NULL,
    followers_count INT NOT NULL DEFAULT 0,
    following_count INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_user_follow_stats
        PRIMARY KEY (user_id),
    
    CONSTRAINT fk_user_follow_stats_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
);


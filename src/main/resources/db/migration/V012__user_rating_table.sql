CREATE TABLE IF NOT EXISTS user_rating (
    rating_id BIGINT NOT NULL AUTO_INCREMENT,
    rated_user_id BIGINT NOT NULL,
    rated_by_user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    score DECIMAL(1,1) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_rating PRIMARY KEY (rating_id),

    CONSTRAINT fk_user_rating_rated_user
        FOREIGN KEY (rated_user_id)
        REFERENCES user(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_rating_rated_by
        FOREIGN KEY (rated_by_user_id)
        REFERENCES user(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_rating_event
        FOREIGN KEY (event_id)
        REFERENCES event(event_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_rating_summary (
    id BIGINT NOT NULL,              -- rated_user_id or event_id
    total_ratings BIGINT NOT NULL,
    average_score DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE user_rating
ADD CONSTRAINT uk_user_rating_unique
UNIQUE (rated_user_id, rated_by_user_id, event_id);

CREATE INDEX idx_user_rating_user ON user_rating(rated_user_id);
CREATE INDEX idx_user_rating_event ON user_rating(event_id);
CREATE INDEX idx_user_rating_summary_id ON user_rating_summary(id);
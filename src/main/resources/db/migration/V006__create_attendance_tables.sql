CREATE TABLE IF NOT EXISTS user_attendance (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    is_going BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_attendance
        PRIMARY KEY (user_id, event_id),

    CONSTRAINT fk_user_attendance_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_attendance_event
        FOREIGN KEY (event_id)
            REFERENCES event(event_id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_attendance_stats (
    event_id BIGINT NOT NULL,
    attendees_count INT NOT NULL DEFAULT 0,
    going_count INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_event_attendance_stats
        PRIMARY KEY (event_id),

    CONSTRAINT fk_event_attendance_stats_event
        FOREIGN KEY (event_id)
            REFERENCES event(event_id)
            ON DELETE CASCADE
);


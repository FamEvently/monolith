-- Reference table: reasons
CREATE TABLE IF NOT EXISTS n_moderate_event_reason (
    moderate_event_reason_id BIGINT NOT NULL AUTO_INCREMENT,
    reason_name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_moderate_event_reason
        PRIMARY KEY (moderate_event_reason_id),

    CONSTRAINT uk_moderate_event_reason_name
        UNIQUE (reason_name)
);

INSERT INTO n_moderate_event_reason (reason_name)
VALUES
    ('SPAM'),
    ('INAPPROPRIATE_CONTENT'),
    ('HATE_SPEECH'),
    ('COPYRIGHT'),
    ('DUPLICATE'),
    ('OTHER');

-- Reference table: status
CREATE TABLE IF NOT EXISTS n_moderate_event_status (
    moderate_event_status_id BIGINT NOT NULL AUTO_INCREMENT,
    status_name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_moderate_event_status
        PRIMARY KEY (moderate_event_status_id),

    CONSTRAINT uk_moderate_event_status_name
        UNIQUE (status_name)
);

INSERT INTO n_moderate_event_status (status_name)
VALUES
    ('PENDING'),
    ('APPROVED'),
    ('REJECTED');

-- Main table: moderation events
CREATE TABLE IF NOT EXISTS moderate_event (
    moderation_id         BIGINT NOT NULL AUTO_INCREMENT,
    event_id              BIGINT NOT NULL,
    moderation_status_id  BIGINT NOT NULL,
    moderation_reason_id  BIGINT NOT NULL,
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_moderate_event
        PRIMARY KEY (moderation_id),

    CONSTRAINT fk_moderate_event_status
        FOREIGN KEY (moderation_status_id)
        REFERENCES n_moderate_event_status (moderate_event_status_id),

    CONSTRAINT fk_moderate_event_reason
        FOREIGN KEY (moderation_reason_id)
        REFERENCES n_moderate_event_reason (moderate_event_reason_id)
);

-- Indexes for faster lookups
CREATE INDEX idx_moderate_event_event_id ON moderate_event (even_

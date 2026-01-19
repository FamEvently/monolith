CREATE TABLE n_event_categories (
                                  event_category_id BIGINT NOT NULL AUTO_INCREMENT,
                                  category_name VARCHAR(100) NOT NULL,

                                  CONSTRAINT pk_event_categories
                                      PRIMARY KEY (event_category_id),

                                  CONSTRAINT uk_event_categories_name
                                      UNIQUE (category_name)
);

INSERT INTO n_event_categories (category_name)
VALUES
    ('STARTUP'),
    ('KIDS_PARTY'),
    ('CINEMA');

CREATE TABLE event (
                       event_id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       overview TEXT NOT NULL,
                       event_category_id BIGINT NOT NULL,
                       date DATETIME NOT NULL,
                       address VARCHAR(500) NOT NULL,
                        location VARCHAR(255) NOT NULL,
                       additional_info TEXT,
                       min_age INT CHECK (min_age >= 0 AND min_age <= 120),
                       max_age INT CHECK (max_age >= 0 AND max_age <= 120),
                       adults_only BOOLEAN NOT NULL DEFAULT FALSE,

    -- FIX: Added 'CURRENT_TIMESTAMP' after DEFAULT
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT pk_event
                           PRIMARY KEY (event_id),

                       CONSTRAINT check_age_range
                           CHECK (
                               max_age IS NULL
                                   OR min_age IS NULL
                                   OR max_age >= min_age
                               ),

                       CONSTRAINT fk_event_user
                           FOREIGN KEY (user_id)
                               -- WARNING: Ensure your table is named 'users', not 'user'
                               REFERENCES users(user_id),

                       CONSTRAINT fk_event_category
                           FOREIGN KEY (event_category_id)
                               REFERENCES n_event_categories(event_category_id),

                       CONSTRAINT unique_event_organizer
                           UNIQUE (event_id, user_id)
);


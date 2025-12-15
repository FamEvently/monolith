CREATE TABLE event_category (
                                  event_category_id BIGINT NOT NULL AUTO_INCREMENT,
                                  category_name VARCHAR(100) NOT NULL,

                                  CONSTRAINT pk_event_categories
                                      PRIMARY KEY (event_category_id),

                                  CONSTRAINT uk_event_categories_name
                                      UNIQUE (category_name)
);

CREATE TABLE event (
                       event_id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       event_overview TEXT NOT NULL,
                       event_category_id BIGINT NOT NULL,
                       event_date DATETIME NOT NULL,
                       event_address VARCHAR(500) NOT NULL,
                       event_additional_info TEXT,
                       min_age INT CHECK (min_age >= 0 AND min_age <= 120),
                       max_age INT CHECK (max_age >= 0 AND max_age <= 120),
                       is_for_adults_only BOOLEAN NOT NULL DEFAULT FALSE,
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
                               REFERENCES user(user_id),

                       CONSTRAINT fk_event_category
                           FOREIGN KEY (event_category_id)
                               REFERENCES event_category(event_category_id),

                       CONSTRAINT unique_event_organizer
                           UNIQUE (event_id, user_id)
)
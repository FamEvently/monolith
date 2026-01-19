CREATE TABLE n_post_types (
                              post_type_id BIGINT NOT NULL AUTO_INCREMENT,
                              type_name VARCHAR(50) NOT NULL,

                              CONSTRAINT pk_post_types
                                  PRIMARY KEY (post_type_id),

                              CONSTRAINT uk_post_types_name
                                  UNIQUE (type_name)
);

INSERT INTO n_post_types (type_name) VALUES ('EVENT'), ('BLOG');

DROP TABLE IF EXISTS post;

CREATE TABLE post (
                      post_id CHAR(36) NOT NULL,
                      message_id CHAR(36) NOT NULL UNIQUE,
                      post_type_id BIGINT NOT NULL,
                      is_organizer BOOLEAN NOT NULL DEFAULT FALSE,
                      parent_post_id CHAR(36) NULL,

                      CONSTRAINT pk_post
                          PRIMARY KEY (post_id),

                      CONSTRAINT fk_post_message
                          FOREIGN KEY (message_id)
                              REFERENCES message(message_id)
                              ON DELETE CASCADE,

                      CONSTRAINT fk_post_type
                          FOREIGN KEY (post_type_id)
                              REFERENCES n_post_types(post_type_id),

                      CONSTRAINT fk_post_parent
                          FOREIGN KEY (parent_post_id)
                              REFERENCES post(post_id)
                              ON DELETE SET NULL
);

CREATE INDEX idx_post_message ON post(message_id);
CREATE INDEX idx_post_type ON post(post_type_id);
CREATE INDEX idx_post_parent ON post(parent_post_id);

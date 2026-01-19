-- Create normalized reactions table
CREATE TABLE n_post_reactions (
    reaction_id BIGINT NOT NULL AUTO_INCREMENT,
    reaction_name VARCHAR(50) NOT NULL,
    
    CONSTRAINT pk_post_reactions
        PRIMARY KEY (reaction_id),
    
    CONSTRAINT uk_post_reactions_name
        UNIQUE (reaction_name)
);

INSERT INTO n_post_reactions (reaction_name) 
VALUES ('HEART'), ('LIKE'), ('AMAZE'), ('SAD'), ('ANGRY'), ('SURPRISED');

-- Create post_likes table (tracks user reactions to posts)
CREATE TABLE post_likes (
    post_id CHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    reaction_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_post_likes
        PRIMARY KEY (post_id, user_id),
    
    CONSTRAINT fk_post_likes_post
        FOREIGN KEY (post_id)
            REFERENCES post(post_id)
            ON DELETE CASCADE,
    
    CONSTRAINT fk_post_likes_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE,
    
    CONSTRAINT fk_post_likes_reaction
        FOREIGN KEY (reaction_id)
            REFERENCES n_post_reactions(reaction_id)
);

CREATE INDEX idx_post_likes_post ON post_likes(post_id);
CREATE INDEX idx_post_likes_user ON post_likes(user_id);

-- Create post_likes_stats table (aggregated like counts per post)
CREATE TABLE post_likes_stats (
    post_id CHAR(36) NOT NULL,
    total_likes_count INT NOT NULL DEFAULT 0,
    heart_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    amaze_count INT NOT NULL DEFAULT 0,
    sad_count INT NOT NULL DEFAULT 0,
    angry_count INT NOT NULL DEFAULT 0,
    surprised_count INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_post_likes_stats
        PRIMARY KEY (post_id),
    
    CONSTRAINT fk_post_likes_stats_post
        FOREIGN KEY (post_id)
            REFERENCES post(post_id)
            ON DELETE CASCADE
);


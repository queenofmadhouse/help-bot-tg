CREATE SCHEMA IF NOT EXISTS cms_entity;

-- Base User Table
CREATE TABLE IF NOT EXISTS cms_entity.users (
                                                    "user_id" BIGSERIAL PRIMARY KEY,
                                                    "first_name" VARCHAR(255) NOT NULL,
                                                    "last_name" VARCHAR(255) NOT NULL
);

-- Admin Table (Inherits from User)
CREATE TABLE IF NOT EXISTS cms_entity.admins (
                                                     "user_id" BIGINT PRIMARY KEY REFERENCES cms_entity.users("user_id"),
                                                     "telegram_user_id" BIGINT UNIQUE NOT NULL
);

-- Regular Requests Table
CREATE TABLE IF NOT EXISTS cms_entity.requests (
                                                               "id" BIGSERIAL PRIMARY KEY,
                                                               "telegram_chat_id" BIGINT NOT NULL,
                                                               "telegram_user_name" VARCHAR(255),
                                                               "telegram_user_pronouns" VARCHAR(50),
                                                               "request_date" TIMESTAMP NOT NULL,
                                                               "request_text" TEXT,
                                                               "is_urgent" BOOLEAN DEFAULT FALSE,
                                                               "related_to" BIGINT,
                                                               "in_work" BOOLEAN DEFAULT FALSE,
                                                               "in_the_archive" BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS cms_entity.messages (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   request_id BIGINT REFERENCES cms_entity.requests(id),
                                                   user_chat_id BIGINT NOT NULL,
                                                   admin_chat_id BIGINT,
                                                   from_admin BOOLEAN NOT NULL,
                                                   message_text TEXT NOT NULL,
                                                   timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

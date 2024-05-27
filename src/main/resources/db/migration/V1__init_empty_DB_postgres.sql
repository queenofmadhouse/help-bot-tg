CREATE SCHEMA IF NOT EXISTS cms_entity;

-- Base User Table
CREATE TABLE IF NOT EXISTS cms_entity.users (
                                                    "user_id" BIGSERIAL PRIMARY KEY,
                                                    "first_name" VARCHAR(255) NOT NULL,  -- Adjust length as needed
                                                    "last_name" VARCHAR(255) NOT NULL,
                                                    "email" VARCHAR(255) UNIQUE NOT NULL,
                                                    "password" VARCHAR(255) NOT NULL   -- Store password hashes
);

-- Admin Table (Inherits from User)
CREATE TABLE IF NOT EXISTS cms_entity.admins (
                                                     "user_id" BIGINT PRIMARY KEY REFERENCES cms_entity.users("user_id"),  -- Foreign Key
                                                     "telegram_user_id" VARCHAR(255) UNIQUE NOT NULL --Assuming Telegram user IDs are strings
);

-- Regular Requests Table
CREATE TABLE IF NOT EXISTS cms_entity.regular_requests (
                                                               "id" BIGSERIAL PRIMARY KEY,
                                                               "telegram_chat_id" BIGINT NOT NULL,
                                                               "telegram_user_name" VARCHAR(255),
                                                               "telegram_user_pronouns" VARCHAR(50),  -- Short length for pronouns
                                                               "request_date" TIMESTAMP NOT NULL,
                                                               "request_text" TEXT,  -- Use TEXT for potentially long requests
                                                               "is_solved" BOOLEAN DEFAULT FALSE
);

-- Urgent Requests Table (Similar structure to regular_requests)
CREATE TABLE IF NOT EXISTS cms_entity.urgent_requests (
                                                              "id" BIGSERIAL PRIMARY KEY,
                                                              "telegram_chat_id" BIGINT NOT NULL,
                                                              "telegram_user_name" VARCHAR(255),
                                                              "telegram_user_pronouns" VARCHAR(50),
                                                              "request_date" TIMESTAMP NOT NULL,
                                                              "request_text" TEXT,
                                                              "is_solved" BOOLEAN DEFAULT FALSE
);

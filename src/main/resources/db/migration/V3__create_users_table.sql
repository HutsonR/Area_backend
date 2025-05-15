CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) NOT NULL UNIQUE,
  name          VARCHAR(200) NOT NULL,
  password_hash VARCHAR(60) NOT NULL,
  created_at    TIMESTAMP WITH TIME ZONE DEFAULT now()
);
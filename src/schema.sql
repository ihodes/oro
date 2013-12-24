CREATE EXTENSION "uuid-ossp";


CREATE TABLE users (
       uuid         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       email        TEXT UNIQUE NOT NULL,
       password     TEXT NOT NULL, 
       api_secret   TEXT UNIQUE NOT NULL,
       api_public   TEXT UNIQUE NOT NULL,
       created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
       root         BOOLEAN DEFAULT false
);

CREATE TABLE transactions (
       uuid          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uuid     UUID REFERENCES users NOT NULL,
       amount        INTEGER NOT NULL,
       source        TEXT NOT NULL,
       destination   TEXT NOT NULL,
       captured_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
       refunded_at   TIMESTAMP WITHOUT TIME ZONE,
       created_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE customers (
       uuid        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uud    UUID REFERENCES users NOT NULL,
       name        TEXT,
       created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE cards (
       uuid           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uuid      UUID REFERENCES users NOT NULL,
       customer_uuid  UUID REFERENCES users,
       name           TEXT,
       number         TEXT,
       type           TEXT,
       expiration     TEXT,
       ccv            TEXT,
       created_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE plans (
       uuid         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uud     UUID REFERENCES users NOT NULL,
       amount       INTEGER,
       name         TEXT,
       description  TEXT,
       interval     TEXT,
       created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE subscriptions (
       uuid            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uud        UUID REFERENCES users NOT NULL,
       plan_uud        UUID REFERENCES plans NOT NULL,
       customer_uuid   UUID REFERENCES customers NOT NULL,
       created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE tokens (
       uuid         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uud     UUID REFERENCES users NOT NULL,
       card_uud     UUID REFERENCES cards NOT NULL,
       token        TEXT,
       created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE EXTENSION "uuid-ossp";

CREATE TABLE users (
       uuid        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       email       TEXT UNIQUE NOT NULL,
       password    TEXT NOT NULL, 
       api_secret  TEXT UNIQUE NOT NULL,
       api_public  TEXT UNIQUE NOT NULL
);

CREATE TABLE transactions (
       uuid        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_uuid   UUID REFERENCES users NOT NULL,
       amount      INTEGER NOT NULL,
       source      text NOT NULL,
       destination text NOT NULL
);

CREATE TABLE customers (
       uuid        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       name        text
);

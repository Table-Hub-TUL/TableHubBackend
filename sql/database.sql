CREATE EXTENSION IF NOT EXISTS postgis;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_status') THEN
CREATE TYPE user_status AS ENUM ('active', 'banned', 'deleted');
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'cuisine') THEN
CREATE TYPE cuisine AS ENUM ('italian', 'japanese', 'polish', 'mexican');
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'section_name') THEN
CREATE TYPE section_name AS ENUM ('main', 'garden', 'vip');
END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'table_status') THEN
CREATE TYPE table_status AS ENUM ('available', 'occupied', 'unknown');
END IF;
END $$;

CREATE TABLE IF NOT EXISTS address (
                                       id bigint PRIMARY KEY,
                                       street_number int NOT NULL,
                                       apartment_number int NULL,
                                       street varchar NOT NULL,
                                       city varchar NOT NULL,
                                       postal_code varchar NOT NULL,
                                       country varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS "user" (
                                      id bigint PRIMARY KEY,
                                      auth_ref text NOT NULL,
                                      email varchar UNIQUE NOT NULL,
                                      name varchar,
                                      registered_at timestamp with time zone DEFAULT now(),
    points int DEFAULT 0,
    status user_status DEFAULT 'active'
    );

CREATE TABLE IF NOT EXISTS restaurant (
                                          id bigint PRIMARY KEY,
                                          name varchar NOT NULL,
                                          cuisine cuisine NOT NULL,
                                          address_id bigint,
                                          location geometry(Point, 4326) NOT NULL,
    CONSTRAINT fk_restaurant_address_id FOREIGN KEY (address_id) REFERENCES address(id)
    );

CREATE TABLE IF NOT EXISTS restaurant_section (
                                                  id bigint PRIMARY KEY,
                                                  name section_name NOT NULL,
                                                  restaurant_id bigint NOT NULL,
                                                  CONSTRAINT fk_restaurant_section_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
    );

CREATE TABLE IF NOT EXISTS restaurant_table (
                                                id bigint PRIMARY KEY,
                                                restaurant_section_id bigint NOT NULL,
                                                status table_status NOT NULL,
                                                position_x real,
                                                position_y real,
                                                capacity int,
                                                CONSTRAINT fk_restaurant_table_section_id FOREIGN KEY (restaurant_section_id) REFERENCES restaurant_section(id)
    );

CREATE TABLE IF NOT EXISTS action (
                                      id bigint PRIMARY KEY,
                                      name varchar NOT NULL,
                                      points smallint NOT NULL
);

CREATE TABLE IF NOT EXISTS points_action (
                                             id bigint PRIMARY KEY,
                                             user_id bigint NOT NULL,
                                             timestamp timestamp with time zone NOT NULL,
                                             action_id bigint NOT NULL,
                                             CONSTRAINT fk_points_action_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_points_action_action FOREIGN KEY (action_id) REFERENCES action(id)
    );

CREATE INDEX IF NOT EXISTS idx_restaurant_location ON restaurant USING GIST (location);

INSERT INTO address (id, street_number, apartment_number, street, city, postal_code, country)
VALUES
    (1, 1, NULL, 'ul. Miodowa', 'Warszawa', '00-001', 'Polska'),
    (2, 4,2 ,'ul. Długa ', 'Kraków', '30-001', 'Polska');

INSERT INTO "user" (id, auth_ref, email, name, registered_at, points, status)
VALUES
    (1, 'auth0|abc123', 'jan.kowalski@example.com', 'Jan Kowalski', NOW(), 100, 'active'),
    (2, 'auth0|xyz456', 'anna.nowak@example.com', 'Anna Nowak', NOW(), 50, 'active');

INSERT INTO restaurant (id, name, cuisine, address_id, location)
VALUES
    (1, 'Trattoria Roma', 'italian', 1, ST_SetSRID(ST_MakePoint(21.0122, 52.2297), 4326)),
    (2, 'Sushi Garden', 'japanese', 2, ST_SetSRID(ST_MakePoint(19.9400, 50.0619), 4326));

INSERT INTO restaurant_section (id, name, restaurant_id)
VALUES
    (1, 'main', 1),
    (2, 'vip', 1),
    (3, 'garden', 2);

INSERT INTO restaurant_table (id, restaurant_section_id, status, position_x, position_y, capacity)
VALUES
    (1, 1, 'available', 1, 1, 4),
    (2, 1, 'reserved', 2, 2, 2),
    (3, 2, 'occupied', 3, 3, 6),
    (4, 3, 'available', 4, 4, 4);

INSERT INTO action (id, name, points)
VALUES
    (1, 'Confirm empty tables', 10),
    (2, 'Add empty tables', 20),
    (3, 'Recommend new restaurant', 30);

INSERT INTO points_action (id, user_id, timestamp, action_id)
VALUES
    (1, 1, NOW() - interval '2 days', 1),
    (2, 1, NOW() - interval '1 days', 2),
    (3, 2, NOW(), 3);

ALTER TABLE "user" ADD COLUMN user_name VARCHAR UNIQUE NOT NULL;
ALTER TABLE "user" ADD COLUMN password VARCHAR NOT NULL;

INSERT INTO role (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_ADMIN');
INSERT INTO role (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_USER');
-- Enable PostGIS extension if not already enabled
CREATE EXTENSION IF NOT EXISTS postgis;

-- Insert roles only if they don't exist
INSERT INTO role (name)
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_ADMIN');

INSERT INTO role (name)
SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_USER');

-- Insert addresses only if ID doesn't exist
INSERT INTO address (id, street_number, apartment_number, street, city, postal_code, country)
SELECT * FROM (VALUES
    (3, 15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
    (4, 22, 5, 'ul. Floriańska', 'Kraków', '31-021', 'Polska'),
    (5, 8, NULL, 'ul. Nowy Świat', 'Warszawa', '00-497', 'Polska')
) AS a(id, street_number, apartment_number, street, city, postal_code, country)
WHERE NOT EXISTS (SELECT 1 FROM address WHERE id = a.id);

-- Insert restaurants only if ID doesn't exist
INSERT INTO restaurant (id, name, cuisine_name, address_id, location, rating)
SELECT * FROM (VALUES
    (3, 'Pierogi Paradise', 'POLISH', 3, ST_SetSRID(ST_MakePoint(19.457, 51.772), 4326), 4.7),
    (4, 'Casa Italiana', 'ITALIAN', 4, ST_SetSRID(ST_MakePoint(19.460, 51.768), 4326), 4.3),
    (5, 'Taco Fiesta', 'MEXICAN', 5, ST_SetSRID(ST_MakePoint(19.455, 51.765), 4326), 4.1)
) AS r(id, name, cuisine_name, address_id, location, rating)
WHERE NOT EXISTS (SELECT 1 FROM restaurant WHERE id = r.id);

-- Insert restaurant sections only if ID doesn't exist
INSERT INTO restaurant_section (id, name, restaurant_id)
SELECT * FROM (VALUES
    (4, 'MAIN', 3),
    (5, 'GARDEN', 3),
    (6, 'VIP', 3),
    (7, 'MAIN', 4),
    (8, 'VIP', 4),
    (9, 'MAIN', 5),
    (10, 'GARDEN', 5)
) AS s(id, name, restaurant_id)
WHERE NOT EXISTS (SELECT 1 FROM restaurant_section WHERE id = s.id);

-- Insert restaurant tables only if ID doesn't exist
INSERT INTO restaurant_table (id, restaurant_section_id, status, position_x, position_y, capacity)
SELECT * FROM (VALUES
    (5, 4, 'AVAILABLE', 1.0, 10.0, 4),
    (6, 4, 'OCCUPIED', 1.0, 20.0, 2),
    (7, 5, 'AVAILABLE', 1.0, 20.0, 6),
    (8, 6, 'AVAILABLE', 1.0, 30.0, 2),
    (9, 7, 'AVAILABLE', 30.0, 1.0, 4),
    (10, 7, 'OCCUPIED', 20.0, 10.0, 4),
    (11, 8, 'AVAILABLE', 10.0, 10.0, 8),
    (12, 9, 'AVAILABLE', 10.0, 20.0, 4),
    (13, 10, 'AVAILABLE', 20.0, 30.0, 6)
) AS t(id, section_id, status, x, y, cap)
WHERE NOT EXISTS (SELECT 1 FROM restaurant_table WHERE id = t.id);

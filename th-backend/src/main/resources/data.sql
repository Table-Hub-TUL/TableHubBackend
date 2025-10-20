CREATE EXTENSION IF NOT EXISTS postgis;

INSERT INTO role (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO role (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO role (name) VALUES ('ROLE_OWNER') ON CONFLICT (name) DO NOTHING;


INSERT INTO address (street_number, apartment_number, street, city, postal_code, country)
VALUES
    (15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
    (22, 5, 'ul. Legionów', 'Łódź', '90-423', 'Polska'),
    (8, NULL, 'ul. Zgierska', 'Łódź', '91-347', 'Polska');

INSERT INTO restaurant (name, cuisine_name, address_id, location, rating)
VALUES
    ('Pierogi Paradise', 'POLISH', 1, ST_SetSRID(ST_MakePoint(19.457, 51.772), 4326), 4.7),
    ('Casa Italiana', 'ITALIAN', 2, ST_SetSRID(ST_MakePoint(19.460, 51.768), 4326), 4.3),
    ('Taco Fiesta', 'MEXICAN', 3, ST_SetSRID(ST_MakePoint(19.455, 51.765), 4326), 4.1);

INSERT INTO restaurant_section (name, restaurant_id)
VALUES
    ('MAIN', 1),
    ('GARDEN', 1),
    ('VIP', 1),
    ('MAIN', 2),
    ('VIP', 2),
    ('MAIN', 3),
    ('GARDEN', 3);

INSERT INTO restaurant_table (restaurant_section_id, status, position_x, position_y, capacity)
VALUES
    (1, 'AVAILABLE', 1.0, 10.0, 4),
    (1, 'OCCUPIED', 1.0, 20.0, 2),
    (1, 'AVAILABLE', 10.0, 10.0, 4),
    (1, 'AVAILABLE', 10.0, 20.0, 2),
    (1, 'AVAILABLE', 20.0, 10.0, 4),
    (1, 'AVAILABLE', 20.0, 20.0, 2),
    (2, 'AVAILABLE', 1.0, 1.0, 4),
    (2, 'AVAILABLE', 1.0, 10.0, 4),
    (2, 'AVAILABLE', 20.0, 1.0, 4),
    (2, 'AVAILABLE', 20.0, 10.0, 6),
    (3, 'AVAILABLE', 10.0, 10.0, 2),
    (3, 'AVAILABLE', 10.0, 20.0, 2),
    (4, 'AVAILABLE', 1.0, 1.0, 4),
    (4, 'AVAILABLE', 1.0, 10.0, 4),
    (4, 'AVAILABLE', 1.0, 20.0, 3),
    (4, 'OCCUPIED', 20.0, 1.0, 2),
    (4, 'OCCUPIED', 20.0, 10.0, 3),
    (4, 'OCCUPIED', 20.0, 20.0, 6),
    (5, 'AVAILABLE', 10.0, 10.0, 8),
    (6, 'AVAILABLE', 10.0, 20.0, 4),
    (6, 'AVAILABLE', 1.0, 20.0, 4),
    (6, 'AVAILABLE', 20.0, 20.0, 4),
    (7, 'AVAILABLE', 1.0, 1.0, 6),
    (7, 'AVAILABLE', 1.0, 10.0, 2),
    (7, 'AVAILABLE', 1.0, 20.0, 4);

INSERT INTO users (user_name, password, email, registered_at, points, status)
VALUES ('admin', '$2a$12$Tuxry0MXlpH53itkfLGrcecUjXq1KdCSpixRssVnRsalOi.yGjhhK', 'admin@tablehub.com', NOW(), 0, 'ACTIVE');

INSERT INTO users_roles (roles_id, app_user_id)
VALUES ((SELECT id FROM role WHERE name = 'ROLE_ADMIN'), 1)
    ON CONFLICT (roles_id, app_user_id) DO NOTHING;
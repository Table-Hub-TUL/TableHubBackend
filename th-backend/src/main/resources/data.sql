-- Enable PostGIS extension if not already enabled
CREATE EXTENSION IF NOT EXISTS postgis;

-- Insert or do nothing if role exists
INSERT INTO role (id, name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER'),
       (3, 'ROLE_OWNER')
    ON CONFLICT (id) DO NOTHING;


-- Insert or update addresses
INSERT INTO address (id, street_number, apartment_number, street, city, postal_code, country)
VALUES (3, 15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
       (4, 22, 5, 'ul. Legionów', 'Łódź', '90-423', 'Polska'),
       (5, 8, NULL, 'ul. Zgierska', 'Łódź', '91-347', 'Polska')
    ON CONFLICT (id) DO UPDATE SET street_number      = EXCLUDED.street_number,
                            apartment_number = EXCLUDED.apartment_number,
                            street           = EXCLUDED.street,
                            city             = EXCLUDED.city,
                            postal_code      = EXCLUDED.postal_code,
                            country          = EXCLUDED.country;

-- Insert or update restaurants
INSERT INTO restaurant (id, name, cuisine_name, address_id, location, rating)
VALUES (3, 'Pierogi Paradise', 'POLISH', 3, ST_SetSRID(ST_MakePoint(19.457, 51.772), 4326), 4.7),
       (4, 'Casa Italiana', 'ITALIAN', 4, ST_SetSRID(ST_MakePoint(19.460, 51.768), 4326), 4.3),
       (5, 'Taco Fiesta', 'MEXICAN', 5, ST_SetSRID(ST_MakePoint(19.455, 51.765), 4326), 4.1)
    ON CONFLICT (id) DO UPDATE SET name         = EXCLUDED.name,
                            cuisine_name = EXCLUDED.cuisine_name,
                            address_id   = EXCLUDED.address_id,
                            location     = EXCLUDED.location,
                            rating       = EXCLUDED.rating;

-- Insert or update restaurant sections
INSERT INTO restaurant_section (id, name, restaurant_id)
VALUES (4, 'MAIN', 3),
       (5, 'GARDEN', 3),
       (6, 'VIP', 3),
       (7, 'MAIN', 4),
       (8, 'VIP', 4),
       (9, 'MAIN', 5),
       (10, 'GARDEN', 5)
    ON CONFLICT (id) DO UPDATE SET name          = EXCLUDED.name,
                            restaurant_id = EXCLUDED.restaurant_id;

-- Insert or update restaurant tables
INSERT INTO restaurant_table (id, restaurant_section_id, status, pos_x, pos_y, capacity)
VALUES (5, 4, 'AVAILABLE', 1.0, 10.0, 4),
       (6, 4, 'OCCUPIED', 1.0, 20.0, 2),
       (7, 4, 'AVAILABLE', 10.0, 10.0, 4),
       (8, 4, 'AVAILABLE', 10.0, 20.0, 2),
       (9, 4, 'AVAILABLE', 20.0, 10.0, 4),
       (10, 4, 'AVAILABLE', 20.0, 20.0, 2),
       (11, 5, 'AVAILABLE', 1.0, 1.0, 4),
       (12, 5, 'AVAILABLE', 1.0, 10.0, 4),
       (13, 5, 'AVAILABLE', 20.0, 1.0, 4),
       (14, 5, 'AVAILABLE', 20.0, 10.0, 6),
       (15, 6, 'AVAILABLE', 10.0, 10.0, 2),
       (16, 6, 'AVAILABLE', 10.0, 20.0, 2),
       (17, 7, 'AVAILABLE', 1.0, 1.0, 4),
       (18, 7, 'AVAILABLE', 1.0, 10.0, 4),
       (19, 7, 'AVAILABLE', 1.0, 20.0, 3),
       (20, 7, 'OCCUPIED', 20.0, 1.0, 2),
       (21, 7, 'OCCUPIED', 20.0, 10.0, 3),
       (22, 7, 'OCCUPIED', 20.0, 20.0, 6),
       (23, 8, 'AVAILABLE', 10.0, 10.0, 8),
       (24, 9, 'AVAILABLE', 10.0, 20.0, 4),
       (25, 9, 'AVAILABLE', 1.0, 20.0, 4),
       (26, 9, 'AVAILABLE', 20.0, 20.0, 4),
       (27, 10, 'AVAILABLE', 1.0, 1.0, 6),
       (28, 10, 'AVAILABLE', 1.0, 10.0, 2),
       (29, 10, 'AVAILABLE', 1.0, 20.0, 4)
    ON CONFLICT (id) DO UPDATE SET restaurant_section_id = EXCLUDED.restaurant_section_id,
                            status                = EXCLUDED.status,
                            pos_x                 = EXCLUDED.pos_x,
                            pos_y                 = EXCLUDED.pos_y,
                            capacity              = EXCLUDED.capacity;

INSERT INTO users (id, user_name, password, email, registered_at, points, status)
VALUES (999, 'admin', '$2a$12$Tuxry0MXlpH53itkfLGrcecUjXq1KdCSpixRssVnRsalOi.yGjhhK', 'admin@tablehub.com', NOW(), 0,
        'ACTIVE')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users_roles (roles_id, app_user_id)
VALUES ((SELECT id FROM role WHERE name = 'ROLE_ADMIN'), 999)
    ON CONFLICT (roles_id, app_user_id) DO NOTHING;
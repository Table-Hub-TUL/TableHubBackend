CREATE EXTENSION IF NOT EXISTS postgis;

INSERT INTO role (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO role (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO role (name) VALUES ('ROLE_OWNER') ON CONFLICT (name) DO NOTHING;

INSERT INTO address (street_number, apartment_number, street, city, postal_code, country)
VALUES
    (15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
    (22, 5, 'ul. Legionów', 'Łódź', '90-423', 'Polska'),
    (8, NULL, 'ul. Zgierska', 'Łódź', '91-347', 'Polska');

INSERT INTO restaurant (name, cuisine_name, address_id, location, rating, region)
VALUES
    ('Pierogi Paradise', 'POLISH', 1, ST_SetSRID(ST_MakePoint(19.457, 51.772), 4326), 4.7, 'tables.europe.poland.1'),
    ('Casa Italiana', 'ITALIAN', 2, ST_SetSRID(ST_MakePoint(19.460, 51.768), 4326), 4.3, 'tables.europe.poland.1'),
    ('Taco Fiesta', 'MEXICAN', 3, ST_SetSRID(ST_MakePoint(19.455, 51.765), 4326), 4.1, 'tables.europe.poland.2');

-- Section Layouts
INSERT INTO section_layout (viewport_width, viewport_height, shape)
VALUES
    -- Layout 1: Rectangular main hall
    (800, 600, 'M 0 0 L 800 0 L 800 600 L 0 600 Z'),
    -- Layout 2: L-shaped garden area
    (900, 700, 'M 0 0 L 600 0 L 600 400 L 900 400 L 900 700 L 0 700 Z'),
    -- Layout 3: Small rectangular VIP room
    (400, 400, 'M 0 0 L 400 0 L 400 400 L 0 400 Z'),
    -- Layout 4: Large rectangular main dining
    (1000, 800, 'M 0 0 L 1000 0 L 1000 800 L 0 800 Z'),
    -- Layout 5: Hexagonal VIP area
    (500, 500, 'M 250 50 L 450 150 L 450 350 L 250 450 L 50 350 L 50 150 Z'),
    -- Layout 6: Rectangular main hall
    (850, 650, 'M 0 0 L 850 0 L 850 650 L 0 650 Z'),
    -- Layout 7: Irregular garden patio
    (750, 600, 'M 0 0 L 700 0 L 750 100 L 750 500 L 650 600 L 0 600 Z');

INSERT INTO restaurant_section (name, restaurant_id, layout_id)
VALUES
    ('MAIN', 1, 1),
    ('GARDEN', 1, 2),
    ('VIP', 1, 3),
    ('MAIN', 2, 4),
    ('VIP', 2, 5),
    ('MAIN', 3, 6),
    ('GARDEN', 3, 7);

-- Points of Interest (POIs) for sections
INSERT INTO section_poi (section_id, id, description, top_left_x, top_left_y, bottom_right_x, bottom_right_y)
VALUES
    -- Pierogi Paradise - MAIN section
    (1, 1.0, 'Main Entrance', 50.0, 50.0, 100.0, 100.0),
    (1, 2.0, 'Restrooms', 750.0, 50.0, 800.0, 150.0),
    (1, 3.0, 'Bar Counter', 350.0, 550.0, 450.0, 600.0),

    -- Pierogi Paradise - GARDEN section
    (2, 4.0, 'Garden Entrance', 300.0, 650.0, 350.0, 700.0),
    (2, 5.0, 'Outdoor Restrooms', 850.0, 650.0, 900.0, 700.0),

    -- Pierogi Paradise - VIP section
    (3, 6.0, 'VIP Entrance', 200.0, 50.0, 250.0, 100.0),

    -- Casa Italiana - MAIN section
    (4, 7.0, 'Main Entrance', 50.0, 50.0, 120.0, 120.0),
    (4, 8.0, 'Restrooms', 950.0, 50.0, 1000.0, 150.0),
    (4, 9.0, 'Wine Bar', 450.0, 750.0, 550.0, 800.0),
    (4, 10.0, 'Kitchen Window', 900.0, 700.0, 1000.0, 800.0),

    -- Casa Italiana - VIP section
    (5, 11.0, 'Private Entrance', 250.0, 50.0, 300.0, 100.0),

    -- Taco Fiesta - MAIN section
    (6, 12.0, 'Main Entrance', 50.0, 50.0, 110.0, 110.0),
    (6, 13.0, 'Restrooms', 800.0, 50.0, 850.0, 120.0),
    (6, 14.0, 'Margarita Bar', 400.0, 600.0, 450.0, 650.0),

    -- Taco Fiesta - GARDEN section
    (7, 15.0, 'Patio Entrance', 50.0, 50.0, 100.0, 100.0),
    (7, 16.0, 'Outdoor Bar', 680.0, 530.0, 720.0, 570.0);

INSERT INTO restaurant_table (restaurant_section_id, status, position_x, position_y, capacity)
VALUES
    (1, 'AVAILABLE', 100.0, 100.0, 4),
    (1, 'OCCUPIED', 100.0, 250.0, 2),
    (1, 'AVAILABLE', 300.0, 100.0, 4),
    (1, 'AVAILABLE', 300.0, 250.0, 2),
    (1, 'AVAILABLE', 500.0, 100.0, 4),
    (1, 'AVAILABLE', 500.0, 250.0, 2),
    (2, 'AVAILABLE', 100.0, 100.0, 4),
    (2, 'AVAILABLE', 100.0, 300.0, 4),
    (2, 'AVAILABLE', 700.0, 500.0, 4),
    (2, 'AVAILABLE', 800.0, 500.0, 6),
    (3, 'AVAILABLE', 150.0, 150.0, 2),
    (3, 'AVAILABLE', 250.0, 250.0, 2),
    (4, 'AVAILABLE', 150.0, 150.0, 4),
    (4, 'AVAILABLE', 150.0, 350.0, 4),
    (4, 'AVAILABLE', 150.0, 550.0, 3),
    (4, 'OCCUPIED', 600.0, 150.0, 2),
    (4, 'OCCUPIED', 600.0, 350.0, 3),
    (4, 'OCCUPIED', 600.0, 550.0, 6),
    (5, 'AVAILABLE', 250.0, 250.0, 8),
    (6, 'AVAILABLE', 300.0, 400.0, 4),
    (6, 'AVAILABLE', 150.0, 400.0, 4),
    (6, 'AVAILABLE', 600.0, 400.0, 4),
    (7, 'AVAILABLE', 150.0, 150.0, 6),
    (7, 'AVAILABLE', 150.0, 350.0, 2),
    (7, 'AVAILABLE', 550.0, 450.0, 4);

INSERT INTO users (user_name, password, email, registered_at, points, status)
VALUES ('admin', '$2a$12$Tuxry0MXlpH53itkfLGrcecUjXq1KdCSpixRssVnRsalOi.yGjhhK', 'admin@tablehub.com', NOW(), 0, 'ACTIVE');

INSERT INTO users_roles (roles_id, app_user_id)
VALUES ((SELECT id FROM role WHERE name = 'ROLE_ADMIN'), 1)
ON CONFLICT (roles_id, app_user_id) DO NOTHING;
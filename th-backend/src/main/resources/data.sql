INSERT INTO role (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_ADMIN');
INSERT INTO role (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_USER');

INSERT INTO address (id, street_number, apartment_number, street, city, postal_code, country)
VALUES
    (3, 15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
    (4, 22, 5, 'ul. Floriańska', 'Kraków', '31-021', 'Polska'),
    (5, 8, NULL, 'ul. Nowy Świat', 'Warszawa', '00-497', 'Polska');

INSERT INTO restaurant (id, name, cuisine_name, address_id, location, rating)
VALUES
    (3, 'Pierogi Paradise', 'POLISH', 3, NULL, 4.7),
    (4, 'Casa Italiana', 'ITALIAN', 4, NULL, 4.3),
    (5, 'Taco Fiesta', 'MEXICAN', 5, NULL, 4.1);

INSERT INTO restaurant_section (id, name, restaurant_id)
VALUES
    (4, 'MAIN', 3),
    (5, 'GARDEN', 3),
    (6, 'VIP', 3),
    (7, 'MAIN', 4),
    (8, 'VIP', 4),
    (9, 'MAIN', 5),
    (10, 'GARDEN', 5);

INSERT INTO restaurant_table (id, restaurant_section_id, status, position_x, position_y, capacity)
VALUES
    (5, 4, 'AVAILABLE', 1.0, 1.0, 4),
    (6, 4, 'OCCUPIED', 2.0, 1.0, 2),
    (7, 5, 'AVAILABLE', 1.0, 2.0, 6),
    (8, 6, 'AVAILABLE', 3.0, 3.0, 2),
    (9, 7, 'AVAILABLE', 1.0, 1.0, 4),
    (10, 7, 'OCCUPIED', 2.0, 2.0, 4),
    (11, 8, 'AVAILABLE', 1.0, 1.0, 8),
    (12, 9, 'AVAILABLE', 1.0, 1.0, 4),
    (13, 10, 'AVAILABLE', 2.0, 2.0, 6);
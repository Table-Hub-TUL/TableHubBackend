INSERT INTO role (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_ADMIN');
INSERT INTO role (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT * FROM role WHERE role.name='ROLE_USER');

INSERT INTO address (id, street_number, apartment_number, street, city, postal_code, country)
VALUES
    (3, 15, NULL, 'ul. Piotrkowska', 'Łódź', '90-001', 'Polska'),
    (4, 22, 5, 'ul. Floriańska', 'Kraków', '31-021', 'Polska'),
    (5, 8, NULL, 'ul. Nowy Świat', 'Warszawa', '00-497', 'Polska');

INSERT INTO restaurant (id, name, cuisine_name, address_id, location, rating)
VALUES
    (3, 'Pierogi Paradise', 'POLISH', 3, null, 4.7),
    (4, 'Casa Italiana', 'ITALIAN', 4, null, 4.3),
    (5, 'Taco Fiesta', 'MEXICAN', 5, null, 4.1);
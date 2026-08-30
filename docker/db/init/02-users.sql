INSERT INTO users (users_id, name, password, email, phone, role)
VALUES
    (
        1,
        'admin',
        '$2a$10$swkM92mgV4nXERrIziqugerSKjC8Hmz8/axcCiVLwAYyIfWNZmh.S',
        'admin@email.com',
        '010-1234-5678',
        'admin'
    )
ON CONFLICT (email) DO UPDATE
SET
    name = EXCLUDED.name,
    password = EXCLUDED.password,
    phone = EXCLUDED.phone,
    role = EXCLUDED.role;

SELECT setval(
    pg_get_serial_sequence('users', 'users_id'),
    COALESCE((SELECT MAX(users_id) FROM users), 1),
    true
);

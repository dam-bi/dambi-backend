INSERT INTO users (users_id, name, password, email, phone)
VALUES
    (
        1,
        'Frontend Tester',
        '$2a$10$pWchNiC9.p0c1rsXNpUvT.eTS/sUCLpuOuMp4hul6Q8/g/fNRduEi',
        'frontend@example.com',
        '010-1234-5678'
    )
ON CONFLICT (email) DO UPDATE
SET
    name = EXCLUDED.name,
    password = EXCLUDED.password,
    phone = EXCLUDED.phone;

SELECT setval(
    pg_get_serial_sequence('users', 'users_id'),
    COALESCE((SELECT MAX(users_id) FROM users), 1),
    true
);

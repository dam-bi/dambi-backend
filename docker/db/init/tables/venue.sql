INSERT INTO venue (venue_id, name, address)
VALUES
    (1, 'Olympic Hall', '424 Olympic-ro, Songpa-gu, Seoul'),
    (2, 'Jamsil Indoor Stadium', '25 Olympic-ro, Songpa-gu, Seoul')
ON CONFLICT (venue_id) DO UPDATE
SET
    name = EXCLUDED.name,
    address = EXCLUDED.address;

SELECT setval(
    pg_get_serial_sequence('venue', 'venue_id'),
    COALESCE((SELECT MAX(venue_id) FROM venue), 1),
    true
);

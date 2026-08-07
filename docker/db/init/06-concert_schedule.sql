INSERT INTO concert_schedule (concert_schedule_id, concert_id, date, show_list)
VALUES
    (1, 1, DATE '2026-08-08', '[{"id":1, "time":"13:00"}, {"id":2, "time":"18:00"}]'),
    (2, 2, DATE '2026-07-10', '[{"id":1, "time":"19:30"}]'),
    (3, 2, DATE '2026-07-11', '[{"id":1, "time":"14:00"}, {"id":2, "time":"19:00"}]'),
    (4, 3, DATE '2026-08-01', '[{"id":1, "time":"18:00"}]'),
    (5, 4, DATE '2026-07-03', '[{"id":1, "time":"19:00"}]'),
    (6, 4, DATE '2026-07-04', '[{"id":1, "time":"18:00"}]'),
    (7, 4, DATE '2026-07-03', '[{"id":1, "time":"17:00"}]'),
    (8, 5, DATE '2026-11-14', '[{"id":1, "time":"19:00"}]')
ON CONFLICT (concert_schedule_id) DO UPDATE
SET
    concert_id = EXCLUDED.concert_id,
    date = EXCLUDED.date,
    show_list = EXCLUDED.show_list;

SELECT setval(
    pg_get_serial_sequence('concert_schedule', 'concert_schedule_id'),
    COALESCE((SELECT MAX(concert_schedule_id) FROM concert_schedule), 1),
    true
);

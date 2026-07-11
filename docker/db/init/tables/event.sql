INSERT INTO event (event_id, concert_id, title, description)
VALUES
    (
        1,
        1,
        'Early Bird Open',
        'Discount event for the first Seoul Summer Live reservations.'
    ),
    (
        2,
        2,
        'Encore Ticket Alert',
        'Frontend seed event tied to the Night Festival Encore concert.'
    ),
    (
        3,
        3,
        'Day6 ',
        '데이식스 이벤트 특가'
    )
ON CONFLICT (event_id) DO UPDATE
SET
    concert_id = EXCLUDED.concert_id,
    title = EXCLUDED.title,
    description = EXCLUDED.description;

SELECT setval(
    pg_get_serial_sequence('event', 'event_id'),
    COALESCE((SELECT MAX(event_id) FROM event), 1),
    true
);

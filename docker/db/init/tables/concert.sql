INSERT INTO concert (
    concert_id,
    venue_id,
    img_url,
    title,
    description,
    booking_cnt,
    created_at,
    running_time,
    start_date,
    end_date,
    age_rating,
    price,
    show_list
)
VALUES
    (
        1,
        1,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782454329/26008688_p_eo5sww.gif',
        '〈현역가왕3〉 전국투어 콘서트 - 대전',
        '예매가능시간 관람 3시간 전까지-2026년 8월 8일(토) 1PM, 6PM',
        8,
        '2026-06-26',
        120,
        '2026-08-08',
        '2026-08-08',
        '8세이상 관람가능',
        99000,
        '[
          {"showTime":"2026-08-15T19:00:00","label":"SAT 7PM"},
          {"showTime":"2026-08-16T18:00:00","label":"SUN 6PM"}
        ]'::json
    ),
    (
        2,
        2,
        'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=1200&q=80',
        'Night Festival Encore',
        'Second seed concert with a different venue and schedule.',
        58,
        '2026-07-02 10:30:00',
        150,
        '2026-09-05 18:00:00',
        '2026-09-05 20:30:00',
        'All',
        77000,
        '[
          {"showTime":"2026-09-05T18:00:00","label":"SAT 6PM"}
        ]'::json
    ),
    (
        3,
        1,
        'https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=1200&q=80',
        'DAY6 The Decade Finale',
        'Third seed concert used by the Day6 local event fixture.',
        73,
        '2026-07-03 14:00:00',
        180,
        '2026-10-03 18:00:00',
        '2026-10-03 21:00:00',
        '12+',
        121000,
        '[
          {"showTime":"2026-10-03T18:00:00","label":"SAT 6PM"}
        ]'::json
    )
ON CONFLICT (concert_id) DO UPDATE
SET
    venue_id = EXCLUDED.venue_id,
    img_url = EXCLUDED.img_url,
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    booking_cnt = EXCLUDED.booking_cnt,
    created_at = EXCLUDED.created_at,
    running_time = EXCLUDED.running_time,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    age_rating = EXCLUDED.age_rating,
    price = EXCLUDED.price,
    show_list = EXCLUDED.show_list;

SELECT setval(
    pg_get_serial_sequence('concert', 'concert_id'),
    COALESCE((SELECT MAX(concert_id) FROM concert), 1),
    true
);

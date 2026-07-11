INSERT INTO users (users_id, name, password, email, phone_number)
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
    phone_number = EXCLUDED.phone_number;

INSERT INTO venue (venue_id, name, address)
VALUES
    (1, 'Olympic Hall', '424 Olympic-ro, Songpa-gu, Seoul'),
    (2, 'Jamsil Indoor Stadium', '25 Olympic-ro, Songpa-gu, Seoul')
ON CONFLICT (venue_id) DO UPDATE
SET
    name = EXCLUDED.name,
    address = EXCLUDED.address;

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
        'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=1200&q=80',
        'Seoul Summer Live',
        'Local Docker seed concert for frontend integration testing.',
        124,
        '2026-07-01 09:00:00',
        120,
        '2026-08-15 19:00:00',
        '2026-08-15 21:00:00',
        '12+',
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

INSERT INTO event (event_id, concert_id, title, description)
SELECT
    seed.event_id,
    seed.concert_id,
    seed.title,
    seed.description
FROM (
    VALUES
        (
            1,
            1,
            '〈현역가왕3〉 전국투어 콘서트 - 대전',
            '이벤트설명'
        ),
        (
            2,
            2,
            '이벤트: Charlie Puth - Whatever＇s Clever! World Tour in Seoul',
            '공연장에서 진행되는 모든 무대 연출, 공연 시간, 셋리스트 등은 아티스트의 요청과 결정에 따라 진행됩니다.'
        ),
        (
            3,
            5,
            '이벤트: DAY6 10th Anniversary Tour〈The DECADE〉FINALE in SEOUL',
            '데이식스 노래 playList'
        ),
        (
            4,
            5,
            '!특가 싸이흠뻑쇼 SUMMERSWAG2026 - 수원!',
            '한 여름을 책임질 싸이의 쇼'
        ),
        (
            5,
            6,
            '이벤트: 뮤지컬 드라큘라 (Dracula：The Musical)',
            '감미로운 음악. '
        )
) AS seed(event_id, concert_id, title, description)
JOIN concert c ON c.concert_id = seed.concert_id
ON CONFLICT (event_id) DO UPDATE
SET
    concert_id = EXCLUDED.concert_id,
    title = EXCLUDED.title,
    description = EXCLUDED.description;

SELECT setval(
    pg_get_serial_sequence('users', 'users_id'),
    COALESCE((SELECT MAX(users_id) FROM users), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('venue', 'venue_id'),
    COALESCE((SELECT MAX(venue_id) FROM venue), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('concert', 'concert_id'),
    COALESCE((SELECT MAX(concert_id) FROM concert), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('event', 'event_id'),
    COALESCE((SELECT MAX(event_id) FROM event), 1),
    true
);

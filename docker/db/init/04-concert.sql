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
    age_rating
)
VALUES
    (
        1,
        1,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782454329/26008688_p_eo5sww.gif',
        '현역가왕3 전국투어 콘서트 - 대전',
        '예매가능시간: 관람 3시간 전까지-2026년 8월 8일(토) 1PM, 6PM',
        12840,
        DATE'2026-06-26',
        360,
        DATE '2026-08-08',
        DATE '2026-08-08',
        '8세이상 관람가능'
    ),
    (
        2,
        2,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782456053/L0000142_p_oqustx.gif',
        '뮤지컬 드라큘라 (Dracula：the Musical)',
        '본 공연은 LG아트센터 서울 운영 공연으로, 예매대기 서비스 및 취소표 대기예매 서비스가 제공되지 않습니다.',
        15600,
        DATE'2026-06-05',
        165,
        DATE '2026-07-10',
        DATE '2026-10-18',
        '14세이상 관람가능'
    ),
    (
        3,
        3,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782457274/psy_bemhbx.gif',
        '싸이흠뻑쇼 SUMMERSWAG2026 - 수원',
        '예매가능시간: 7월17일(금) 관람일시까지/7월 11일(토) 관람일시까지',
        15600,
        DATE'2026-05-10',
        18000,
        DATE '2026-08-01',
        DATE '2026-08-02',
        '전체관람가'
    ),
    (
        4,
        4,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782457274/psy_bemhbx.gif',
        'DAY6 10th Anniversary Tour〈The DECADE〉FINAL in SEOUL',
        '2026년 06월 01일 00시 00분~2026년 07월 05일 23시 59분까지. 무통장입금 결제가 불가합니다.',
        4891,
        DATE'2026-05-10',
        180,
        DATE '2026-07-03',
        DATE '2026-07-05',
        '만 7세이상'
    ),
    (
        5,
        5,
        'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782458068/charli_wciywi.gif',
        'Charlie Puth - Whatever／Clever! World Tour in Seoul',
        '공연일에 진행되는 모든 무대 연출, 공연 시간, 셋리스트 등은 아티스트의 요청과 결정에 따라 진행됩니다.',
        590,
        DATE'2026-06-15',
        240,
        DATE '2026-11-14',
        DATE '2026-11-14',
        '만 7세이상'
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
    age_rating = EXCLUDED.age_rating;

SELECT setval(
    pg_get_serial_sequence('concert', 'concert_id'),
    COALESCE((SELECT MAX(concert_id) FROM concert), 1),
    true
);

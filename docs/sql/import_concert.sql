
INSERT INTO venue (
    venue_id,
    name,
    address
) VALUES
    (
        1,
        '대전 컨벤션센터 제1전시장',
        '주소 정보 없음'
    ),
    (
        2,
        'LG아트센터 서울 LG SIGNATURE 홀',
        '주소 정보 없음'
    ),
    (
        4,
        '수원월드컵경기장 보조경기장',
        '주소 정보 없음'
    ),
    (
        5,
        'KSPO DOME',
        '주소 정보 없음'
    ),
    (
        6,
        '일산 킨텍스 제1전시장 1홀',
        '주소 정보 없음'
    )
ON CONFLICT (venue_id)
DO UPDATE SET
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
    'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782454329/26008688_p_eo5sww.gif',
    '〈현역가왕3〉 전국투어 콘서트 - 대전',
    '예매가능시간 관람 3시간 전까지-2026년 8월 8일(토) 1PM, 6PM',
    8,
    '2026-06-26 00:00:00',
    360,
    '2026-08-08 00:00:00',
    '2026-08-08 00:00:00',
    '8세이상 관람가능',
    143000,
    '[
        {
            "id": 1,
            "date": "2026-08-08",
            "show_list": [
                {
                    "id": 1,
                    "time": "13:00"
                },
                {
                    "id": 2,
                    "time": "18:00"
                }
            ]
        }
    ]'::json
),

(
    2,
    2,
    'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782456053/L0000142_p_oqustx.gif',
    '뮤지컬 드라큘라 (Dracula：The Musical)',
    '※ 본 공연은 LG아트센터 서울 연동 공연으로, 예매대기 서비스 및 취소후 재예매 서비스가 제공되지 않습니다.',
    15600,
    '2026-06-05 00:00:00',
    165,
    '2026-07-10 00:00:00',
    '2026-10-18 00:00:00',
    '14세이상 관람가능',
    80000,
    '[
        {
            "id": 1,
            "date": "2026-07-10",
            "show_list": [
                {
                    "id": 1,
                    "time": "19:30"
                }
            ]
        },
        {
            "id": 2,
            "date": "2026-07-11",
            "show_list": [
                {
                    "id": 1,
                    "time": "14:00"
                },
                {
                    "id": 2,
                    "time": "19:00"
                }
            ]
        }
    ]'::json
),

(
    5,
    4,
    'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782457274/psy_bemhbx.gif',
    '싸이흠뻑쇼 SUMMERSWAG2026 - 수원',
    '예매가능시간: 전일17시(월~토 관람 시)까지/전일 11시(일요일 관람 시)까지',
    15600,
    '2026-05-10 00:00:00',
    18000,
    '2026-08-01 00:00:00',
    '2026-08-02 00:00:00',
    '전체관람가',
    175000,
    '[
        {
            "id": 1,
            "date": "2026-08-01",
            "show_list": [
                {
                    "id": 1,
                    "time": "18:00"
                }
            ]
        }
    ]'::json
),

(
    4,
    5,
    'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782457274/psy_bemhbx.gif',
    'DAY6 10th Anniversary Tour〈The DECADE〉FINALE in SEOUL',
    '2026년 06월 01일 00시 00분~2026년 07월 05일 23시 59분까지.무통장입금 결제가 불가능합니다.',
    4891,
    '2026-05-10 00:00:00',
    180,
    '2026-07-03 00:00:00',
    '2026-07-05 00:00:00',
    '만 7세이상',
    154000,
    '[
        {
            "id": 1,
            "date": "2026-07-03",
            "show_list": [
                {
                    "id": 1,
                    "time": "19:00"
                }
            ]
        },
        {
            "id": 1,
            "date": "2026-07-04",
            "show_list": [
                {
                    "id": 1,
                    "time": "18:00"
                }
            ]
        },
        {
            "id": 1,
            "date": "2026-07-03",
            "show_list": [
                {
                    "id": 1,
                    "time": "17:00"
                }
            ]
        }
    ]'::json
),

(
    6,
    6,
    'https://res.cloudinary.com/dvvryn0ya/image/upload/v1782458068/charli_wciywi.gif',
    'Charlie Puth - Whatever＇s Clever! World Tour in Seoul',
    '공연장에서 진행되는 모든 무대 연출, 공연 시간, 셋리스트 등은 아티스트의 요청과 결정에 따라 진행됩니다.',
    590,
    '2026-06-15 00:00:00',
    240,
    '2026-11-14 00:00:00',
    '2026-11-14 00:00:00',
    '만 7세이상',
    154000,
    '[
        {
            "id": 1,
            "date": "2026-11-14",
            "show_list": [
                {
                    "id": 1,
                    "time": "19:00"
                }
            ]
        }
    ]'::json
)

ON CONFLICT (concert_id)
DO UPDATE SET
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
    pg_get_serial_sequence('venue', 'venue_id'),
    (SELECT MAX(venue_id) FROM venue),
    true
);

SELECT setval(
    pg_get_serial_sequence('concert', 'concert_id'),
    (SELECT MAX(concert_id) FROM concert),
    true
);


COMMIT;
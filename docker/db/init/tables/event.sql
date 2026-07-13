INSERT INTO event (event_id, concert_id, title, description, status)
VALUES
    (
        1,
        1,
        '얼리버드:〈현역가왕3〉 전국투어 콘서트 - 대전',
        '단독 얼리버드 행사! 2026년 7월 15일~20일 총 5일동안의 이벤트 특가 할인!',
        DATE '2026-07-20'
                DATE '2026-07-15',
                '예정',


    ),
    (
        2,
        5,
        '단독 굿즈 증정 이벤트: 찰리 푸스',
        '7월 동안 이 곳에서 예매 시, 추첨을 통해 찰리 푸스 친필 사인 앨범을 추첨을 통해 드립니다!',
        DATE '2026-07-01'
            DATE '2026-07-30',
     '진행중'
    ),
    (
        3,
        3,
        '얼리버드: 싸이흠뻑쇼 SUMMERSWAG2026 - 수원',
        '6월 1일~6월 15일 동안 한정 얼리버드 오픈',
        DATE '2026-06-01'
            DATE '2026-06-15',
        '종료'
    )
ON CONFLICT (event_id) DO UPDATE
SET
    concert_id = EXCLUDED.concert_id,
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    status = EXCLUDED.status;

SELECT setval(
    pg_get_serial_sequence('event', 'event_id'),
    COALESCE((SELECT MAX(event_id) FROM event), 1),
    true
);

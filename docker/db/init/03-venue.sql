INSERT INTO venue (venue_id, name, address)
VALUES
    (1, '대전 컨벤션센터 제2전시장', '대전 유성구 엑스포로 107 , 1층'),
    (2, 'LG아트센터 서울 LG SIGNATURE 홀', '서울 강서구 마곡중앙로 136 LG아트센터 서울'),
    (3, '수원월드컵경기장 보조경기장', '경기 수원시 팔달구 월드컵로'),
    (4, 'KSPO DOME', '서울 송파구 올림픽로 424'),
    (5, '일산 킨텍스 제1전시장', '경기도 고양시 일산서구 킨텍스로 217-60 킨텍스')
ON CONFLICT (venue_id) DO UPDATE
SET
    name = EXCLUDED.name,
    address = EXCLUDED.address;

SELECT setval(
    pg_get_serial_sequence('venue', 'venue_id'),
    COALESCE((SELECT MAX(venue_id) FROM venue), 1),
    true
);

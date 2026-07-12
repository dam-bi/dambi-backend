INSERT INTO concert_price (concert_price_id, concert_id, rating, price)
VALUES
    (1, 1, 'VIP', 154000),
    (2, 1, 'R', 143000),
    (3, 2, 'OP', 180000),
    (4, 2, 'VIP', 180000),
    (5, 2, 'R', 150000),
    (6, 2, 'S', 110000),
    (7, 2, 'A', 80000),
    (8, 3, '스탠딩R', 185000),
    (9, 3, '스탠딩S', 175000),
    (10, 3, '지정석', 185000),
    (11, 4, '스탠딩', 154000),
    (12, 4, '지정석', 154000),
    (13, 5, '스탠딩R', 165000),
    (14, 5, '스탠딩S', 154000)
ON CONFLICT (concert_price_id) DO UPDATE
SET
    concert_id = EXCLUDED.concert_id,
    rating = EXCLUDED.rating,
    price = EXCLUDED.price;

SELECT setval(
    pg_get_serial_sequence('concert_price', 'concert_price_id'),
    COALESCE((SELECT MAX(concert_price_id) FROM concert_price), 1),
    true
);

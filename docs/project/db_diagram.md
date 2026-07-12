# DB Diagram Notes

## Concert price/date mapping

`docker/db/init/db.json` uses a richer response shape than the current `concert` table.

- `price` in `db.json` is an array of objects like `[{ rating, price }, ...]`.
- `date` in `db.json` is either one object or an array of objects like `[{ date, show_list }, ...]`.

## Current PostgreSQL mapping

The current schema and JPA mapping store concert data like this:

- `concert.price`: `INTEGER NOT NULL`
  - Stores one representative price only.
  - It cannot directly represent multiple seat grades such as `VIP`, `R`, `S`.
- `concert.start_date`, `concert.end_date`: concert period fields
  - These are not a full replacement for per-show schedules.
- `concert.show_list`: `JSON NOT NULL`
  - Stores the actual show instances shown on the web.
  - This is the current place that corresponds most closely to `db.json.date[*].show_list`.

## Recommended normalized model

If the backend should fully support the `db.json` shape in PostgreSQL, split the data into separate tables instead of forcing it into a single `concert` row.

### concert_price

```sql
concert_price (
  id BIGSERIAL PRIMARY KEY,
  concert_id BIGINT NOT NULL REFERENCES concert(concert_id),
  rating VARCHAR(50) NOT NULL,
  price INTEGER NOT NULL
)
```

- Use this table when one concert has multiple seat grades and prices.
- `concert.price` can remain only if the project intentionally keeps a representative minimum/base price for list screens.

### concert_schedule

```sql
concert_schedule (
  id BIGSERIAL PRIMARY KEY,
  concert_id BIGINT NOT NULL REFERENCES concert(concert_id),
  show_date DATE NOT NULL,
  show_time TIME NOT NULL
)
```

- Use this table when one concert has multiple performance dates and times.
- This maps naturally to the web response shape:
  - `date` -> `show_date`
  - `show_list[].time` -> `show_time`

## Practical rule

Until the schema is normalized:

- treat `concert.price` as a single representative price
- treat `concert.start_date` and `concert.end_date` as the overall run period
- treat `concert.show_list` JSON as the source for per-show schedule data

If the API must return grade-specific prices and date-grouped schedules exactly like `db.json`, prefer `concert_price` and `concert_schedule` over adding more JSON blobs to `concert`.

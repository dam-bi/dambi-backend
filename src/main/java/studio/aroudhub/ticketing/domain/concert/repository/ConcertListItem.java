package studio.aroudhub.ticketing.domain.concert.repository;

import java.time.LocalDateTime;

public record ConcertListItem(
        int concertId,
        String title,
        String imgUrl,
        String venueName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int price
) {
}

package studio.aroudhub.ticketing.domain.event.repository;

import java.time.LocalDate;

public record EventSummary(
        Long id,
        String title,
        String description,
        String badge,
        LocalDate startDate,
        LocalDate endDate,
        Long concertId,
        String concertTitle,
        String imageUrl
) {
}

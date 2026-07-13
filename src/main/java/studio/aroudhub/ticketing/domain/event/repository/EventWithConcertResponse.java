package studio.aroudhub.ticketing.domain.event.repository;

import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

import java.time.LocalDate;

public record EventWithConcertResponse(
        int eventId,
        String title,
        String description,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        // int concertId,
        Concert concert
        /*
        String concertTitle,
        String concertImgUrl,
        LocalDate concertStartDate,
        LocalDate concertEndDate,
        String venueName,
        String title,
        String description
         */
) {
}

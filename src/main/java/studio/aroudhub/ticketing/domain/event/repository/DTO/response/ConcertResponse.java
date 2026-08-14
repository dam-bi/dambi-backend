package studio.aroudhub.ticketing.domain.event.repository.DTO.response;

import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

import java.time.LocalDate;

public record ConcertResponse(
        int concertId,
        String concertTitle,
        String imgUrl,
        String concertDesc,
        String venue,
        int runningTime,
        LocalDate concertStartDate,
        LocalDate concertEndDate,
        String ageRating
) {
    public static ConcertResponse from(Concert concert)
    {
        return new ConcertResponse(
                concert.getConcertId(),
                concert.getTitle(),
                concert.getImgUrl(),
                concert.getDescription(),
                concert.getVenue().getName(),
                concert.getRunningTime(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getAgeRating()
        );
    }
}

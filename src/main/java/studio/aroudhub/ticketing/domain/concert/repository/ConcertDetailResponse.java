package studio.aroudhub.ticketing.domain.concert.repository;

import java.time.LocalDateTime;
import java.util.List;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;

public record ConcertDetailResponse(
        int concertId,
        int venueId,
        String title,
        String imgUrl,
        String description,
        int bookingCount,
        String createdAt,
        String venue,
        int runningTime,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String ageRating,
        List<ConcertPrice> price,
        List<ConcertSchedule> date
) {
    public static ConcertDetailResponse from(Concert concert) {
        return new ConcertDetailResponse(
                concert.getConcertId(),
                concert.getVenue().getVenueId(),
                concert.getTitle(),
                concert.getImgUrl(),
                concert.getDescription(),
                concert.getBookingCnt(),
                concert.getCreatedAt(),
                concert.getVenue().getName(),
                concert.getRunning_time(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getAge_rating(),
                concert.getPrice(),
                concert.getDate()
        );
    }
}

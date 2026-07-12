package studio.aroudhub.ticketing.domain.concert.repository;

import java.time.LocalDateTime;
import java.util.List;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowInfo;

public record ConcertDetailResponse(
        int concertId,
        String title,
        String description,
        String imgUrl,
        String venueName,
        String venueAddress,
        int bookingCount,
        int runningTime,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String ageRating,
        int price,
        List<ShowInfo> showList
) {
    public static ConcertDetailResponse from(Concert concert) {
        return new ConcertDetailResponse(
                concert.getConcertId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getImgUrl(),
                concert.getVenue().getName(),
                concert.getVenue().getAddress(),
                concert.getBookingCnt(),
                concert.getRunning_time(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getAge_rating(),
                concert.getPrice(),
                concert.getShowList()
        );
    }
}

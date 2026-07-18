package studio.aroudhub.ticketing.domain.concert.repository.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;

public record ConcertDetailResponse(
        int concertId, // 프론트에서 concertId는 uuid로 변환 요청.
        String concertTitle,
        String imgUrl,
        String concertDesc,
        int bookingCnt,
        LocalDate createdAt,
        String venue,
        int runningTime,
        LocalDate concertStartDate,
        LocalDate concertEndDate,
        String ageRating,
        List<ConcertPriceResponse> seatList,
        List<ConcertScheduleResponse> schedule

) {
    public static ConcertDetailResponse from(Concert concert) {
        return new ConcertDetailResponse(
                concert.getConcertId(),
                concert.getTitle(),
                concert.getImgUrl(),
                concert.getDescription(),
                concert.getBookingCnt(),
                concert.getCreatedAt(),
                concert.getVenue().getName(),
                concert.getRunningTime(),
                concert.getStartDate(),
                concert.getEndDate(),
                concert.getAgeRating(),
                concert.getPrice().stream()
                        .map(ConcertPriceResponse :: from )
                        .toList(),
                concert.getDate().stream()
                        .map(ConcertScheduleResponse::from)
                        .toList()
        );
    }

}

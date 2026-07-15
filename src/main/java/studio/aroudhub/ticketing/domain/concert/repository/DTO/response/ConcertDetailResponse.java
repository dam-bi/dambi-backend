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
        int bookingCnt,
        String venue,
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
                concert.getBookingCnt(),
                concert.getVenue().getName(),
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

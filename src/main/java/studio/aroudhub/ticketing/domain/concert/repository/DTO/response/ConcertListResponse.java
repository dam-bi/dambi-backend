package studio.aroudhub.ticketing.domain.concert.repository.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

public record ConcertListResponse(
        @JsonProperty("concertId")
        // 프론트에서 concertId는 uuid로 변환 요청.
        int concertId,
        String concertTitle,
        @JsonProperty("imgUrl")
        String imgUrl,
        @JsonProperty("bookingCnt")
        int bookingCnt,
        String venue,
        @JsonProperty("startDate")
        LocalDate concertStartDate,
        @JsonProperty("endDate")
        LocalDate concertEndDate
) {
    public static ConcertListResponse from(Concert concert) {
        return new ConcertListResponse(
                concert.getConcertId(),
                concert.getTitle(),
                concert.getImgUrl(),
                concert.getBookingCnt(),
                concert.getVenue().getName(),
                concert.getStartDate(),
                concert.getEndDate()
        );
    }
}

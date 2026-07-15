package studio.aroudhub.ticketing.domain.concert.repository.DTO.response;

import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;

import java.time.LocalDate;
import java.util.List;

public record ConcertScheduleResponse(
        int concertScheduleId,
        LocalDate date,
        List<ShowListResponse> showList
) {
    public static ConcertScheduleResponse from(ConcertSchedule concertSchedule){
        return new ConcertScheduleResponse(
                concertSchedule.getConcertScheduleId(),
                concertSchedule.getDate(),
                concertSchedule.getShowList().stream()
                        .map(ShowListResponse::from)
                        .toList()
        );
    }
}

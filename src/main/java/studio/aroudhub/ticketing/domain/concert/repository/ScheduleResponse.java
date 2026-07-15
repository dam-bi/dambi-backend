package studio.aroudhub.ticketing.domain.concert.repository;

import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ShowListResponse;

import java.time.LocalDate;
import java.util.List;

public record ScheduleResponse(
        int scheduleId,
        LocalDate date,
        List<ShowListResponse> showList
) {
}

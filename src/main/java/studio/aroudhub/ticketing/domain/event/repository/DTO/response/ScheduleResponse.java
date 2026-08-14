package studio.aroudhub.ticketing.domain.event.repository.DTO.response;

import java.time.LocalDate;
import java.util.List;

public record ScheduleResponse(
        int scheduleId,
        LocalDate date,
        List<ShowTimeResponse> showList
) {
}

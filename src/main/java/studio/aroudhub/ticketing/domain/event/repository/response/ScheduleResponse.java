package studio.aroudhub.ticketing.domain.event.repository.response;

import java.time.LocalDate;
import java.util.List;

public record ScheduleResponse(
        int scheduleId,
        LocalDate date,
        List<ShowTimeResponse> showList
) {
}

package studio.aroudhub.ticketing.domain.concert.repository.entity;

import java.time.LocalTime;

public record ScheduleShowTime(
        int id,
        LocalTime time
) {
}

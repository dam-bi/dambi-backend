package studio.aroudhub.ticketing.domain.event.repository;

import java.time.LocalTime;

public record ShowTimeResponse(
        int showId,
        LocalTime time
) {
}

package studio.aroudhub.ticketing.domain.event.repository.DTO.response;

import java.time.LocalTime;

public record ShowTimeResponse(
        int showId,
        LocalTime time
) {
}

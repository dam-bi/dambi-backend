package studio.aroudhub.ticketing.domain.event.repository;

import java.time.LocalDate;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public record EventListResponse(
        int eventId,
        String eventTitle,
        String concertImg,
        String status,
        LocalDate eventStartDate,
        LocalDate eventEndDate
) {
    public static EventListResponse from(Event event) {
        return new EventListResponse(
                event.getEventId(),
                event.getTitle(),
                event.getConcert().getImgUrl(),
                event.getStatus(),
                event.getStartDate(),
                event.getEndDate()
        );
    }
}

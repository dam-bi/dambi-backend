package studio.aroudhub.ticketing.domain.event.repository;

import java.time.LocalDate;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public record EventList(
        int eventId,
        Concert concert,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
    public static EventList from(Event event) {
        return new EventList(
                event.getEventId(),
                event.getConcert(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getStatus()
        );
    }
}

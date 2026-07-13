package studio.aroudhub.ticketing.domain.event.repository;

import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

import java.time.LocalDate;

public record EventList(
        int eventId,
        int concertId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
    public static EventList from(Event event) {
        return new EventList(
                event.getEventId(),
                event.getConcert().getConcertId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getStatus()
        );
    }
}

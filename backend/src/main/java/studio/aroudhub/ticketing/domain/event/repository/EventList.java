package studio.aroudhub.ticketing.domain.event.repository;

import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public record EventList(
        int eventId,
        int concertId,
        String title,
        String description
) {
    public static EventList from(Event event) {
        return new EventList(
                event.getEventId(),
                event.getConcert().getConcertId(),
                event.getTitle(),
                event.getDescription()
        );
    }
}

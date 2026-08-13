package studio.aroudhub.ticketing.domain.event.repository.response;

import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

import java.time.LocalDate;

public record EventWithConcertResponse(
        int eventId,
        String eventTitle,
        String eventDesc,
        String status,
        LocalDate eventStartDate,
        LocalDate eventEndDate,
        ConcertResponse concert
) {
    public static EventWithConcertResponse from(Event event){
        return new EventWithConcertResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getStatus(),
                event.getStartDate(),
                event.getEndDate(),
                ConcertResponse.from(event.getConcert())
        );
    }
}

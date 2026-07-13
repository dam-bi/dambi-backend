package studio.aroudhub.ticketing.domain.event.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse;
import studio.aroudhub.ticketing.domain.event.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    // /api/events 동작
    public List<EventWithConcertResponse> eventMain() {
        return eventService.findAllWithConcert();
    }
}

package studio.aroudhub.ticketing.domain.event.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
import studio.aroudhub.ticketing.domain.event.service.EventService;

@CrossOrigin(originPatterns = "http://localhost:5173") // crossOrigin 어노테이션 추가.
@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventList> eventMain() {
        return eventService.findAll();
    }
}

package studio.aroudhub.ticketing.domain.event.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.event.repository.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventListResponse;
import studio.aroudhub.ticketing.domain.event.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public Page<EventListResponse> eventMain(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return eventService.findPage(pageable);
    }

    @GetMapping("/{eventID}")
    public EventDetailResponse getEventDetail(@PathVariable int eventID) {
        return eventService.findDetail(eventID);
    }
}

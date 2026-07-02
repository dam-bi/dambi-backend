package studio.aroudhub.ticketing.domain.event.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.aroudhub.ticketing.domain.event.service.EventService;

@RestController
public class EventController {

    @GetMapping(value = "/events")
    public String event_main() {
        return "event main test";
    }
}

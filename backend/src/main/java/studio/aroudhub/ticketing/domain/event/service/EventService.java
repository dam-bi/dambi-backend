package studio.aroudhub.ticketing.domain.event.service;

import java.util.List;
import org.springframework.stereotype.Service;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventList> findAll() {
        return eventRepository.findAllEventLists();
    }
}

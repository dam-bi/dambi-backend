package studio.aroudhub.ticketing.domain.event.service;

import java.util.List;
import org.springframework.stereotype.Service;
import studio.aroudhub.ticketing.domain.event.repository.EventList;

@Service
public class EventService {

    // DB is not wired yet, so EventRepository injection is disabled for now.
    // Switch this back to repository-based loading after datasource/JPA is configured.
    private static final List<EventList> EVENTS = List.of(
            new EventList(
                    1,
                    101,
                    "Early bird discount",
                    "Discount event for early reservations"
            )
    );

    public List<EventList> findAll() {
        return EVENTS;
    }
}

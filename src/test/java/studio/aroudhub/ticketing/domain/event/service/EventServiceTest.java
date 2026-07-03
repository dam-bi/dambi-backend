package studio.aroudhub.ticketing.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.event.repository.EventList;

class EventServiceTest {

    @Test
    void findAll_returnsInMemoryEventListWhileDatabaseIsDisabled() {
        EventService eventService = new EventService();

        List<EventList> result = eventService.findAll();

        assertThat(result).containsExactly(
                new EventList(
                        1,
                        101,
                        "Early bird discount",
                        "Discount event for early reservations"
                )
        );
    }
}

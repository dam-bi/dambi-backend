package studio.aroudhub.ticketing.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;

class EventServiceTest {

    @Test
    void findAll_returnsRepositoryEventLists() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        List<EventList> expected = List.of(
                new EventList(
                        7,
                        33,
                        "Summer package",
                        "Bundle promotion for weekend bookings"
                )
        );

        when(eventRepository.findAllEventLists()).thenReturn(expected);

        List<EventList> result = eventService.findAll();

        assertThat(result).isEqualTo(expected);
    }
}

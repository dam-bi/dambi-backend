package studio.aroudhub.ticketing.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.ConcertResponse;
import studio.aroudhub.ticketing.domain.event.repository.EventList;
import studio.aroudhub.ticketing.domain.event.repository.EventRepository;
import studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

class EventServiceTest {

    @Test
    void findAll_returnsRepositoryEventLists() throws Exception {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        Concert concert = createConcert();
        List<EventList> expected = List.of(
                new EventList(
                        7,
                        concert,
                        "Summer package",
                        "Bundle promotion for weekend bookings",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31),
                        "SCHEDULED"
                )
        );

        when(eventRepository.findAllEventLists()).thenReturn(expected);

        List<EventList> result = eventService.findAll();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findAllWithConcert_returnsRepositoryEventWithConcertViews() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);
        ConcertResponse concert = createConcertResponse();
        List<EventWithConcertResponse> expected = List.of(
                new EventWithConcertResponse(
                        7,
                        "Summer Concert Event",
                        "Bundle promotion for weekend bookings",
                        "SCHEDULED",
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31),
                        concert
                )
        );

        when(eventRepository.findAllEventWithConcertViews()).thenReturn(expected);

        List<EventWithConcertResponse> result = eventService.findAllWithConcert();

        assertThat(result).isEqualTo(expected);
    }

    private ConcertResponse createConcertResponse() {
        return new ConcertResponse(
                33,
                "Summer Concert",
                "https://cdn.example.com/summer.png",
                "Outdoor summer performance",
                "Jamsil Arena",
                180,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "15+"
        );
    }

    private Concert createConcert() throws Exception {
        Concert concert = instantiate(Concert.class);
        Venue venue = instantiate(Venue.class);

        setField(venue, "venueId", 5);
        setField(venue, "name", "Jamsil Arena");
        setField(venue, "address", "Seoul");

        setField(concert, "concertId", 33);
        setField(concert, "venue", venue);
        setField(concert, "title", "Summer Concert");
        setField(concert, "imgUrl", "https://cdn.example.com/summer.png");
        setField(concert, "startDate", LocalDate.of(2026, 8, 1));
        setField(concert, "endDate", LocalDate.of(2026, 8, 3));

        return concert;
    }

    private <T> T instantiate(Class<T> type) throws Exception {
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

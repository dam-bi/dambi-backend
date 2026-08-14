package studio.aroudhub.ticketing.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findEventList_returnsProjectedEventSummary() {
        persistEventGraph();
        entityManager.flush();
        entityManager.clear();

        List<EventListResponse> result = eventRepository.findEventList();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().eventTitle()).isEqualTo("2026 Jazz Night Event");
        assertThat(result.getFirst().concertImg()).isEqualTo("https://cdn.example.com/jazz-night.jpg");
    }

    @Test
    void findEventDetailByEventId_returnsProjectedEventDetail() {
        Event event = persistEventGraph();
        int eventId = getIntField(event, "eventId");
        entityManager.flush();
        entityManager.clear();

        EventDetailResponse result = eventRepository.findEventDetailByEventId(eventId).orElseThrow();

        assertThat(result.eventId()).isEqualTo(eventId);
        assertThat(result.eventTitle()).isEqualTo("2026 Jazz Night Event");
        assertThat(result.concert().concertTitle()).isEqualTo("Jazz Night");
        assertThat(result.concert().venue()).isEqualTo("Olympic Hall");
    }

    @Test
    void findEventPage_returnsProjectedEventSummaryPage() {
        persistEventGraph();
        entityManager.flush();
        entityManager.clear();

        var result = eventRepository.findEventPage(PageRequest.of(0, 10), null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().eventTitle()).isEqualTo("2026 Jazz Night Event");
    }

    private Event persistEventGraph() {
        Venue venue = new Venue();
        setField(venue, "name", "Olympic Hall");
        setField(venue, "address", "123 Seoul Street");
        entityManager.persist(venue);

        Concert concert = new Concert();
        setField(concert, "venue", venue);
        setField(concert, "title", "Jazz Night");
        setField(concert, "imgUrl", "https://cdn.example.com/jazz-night.jpg");
        setField(concert, "description", "Late summer jazz concert");
        setField(concert, "bookingCnt", 12);
        setField(concert, "createdAt", LocalDate.of(2026, 8, 1));
        setField(concert, "runningTime", 120);
        setField(concert, "startDate", LocalDate.of(2026, 9, 10));
        setField(concert, "endDate", LocalDate.of(2026, 9, 12));
        setField(concert, "ageRating", "12+");
        entityManager.persist(concert);

        Event event = new Event();
        setField(event, "concert", concert);
        setField(event, "title", "2026 Jazz Night Event");
        setField(event, "description", "Special event details");
        setField(event, "startDate", LocalDate.of(2026, 9, 10));
        setField(event, "endDate", LocalDate.of(2026, 9, 12));
        setField(event, "status", "진행중");
        entityManager.persist(event);

        return event;
    }

    private int getIntField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read field: " + fieldName, e);
        }
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to set field: " + fieldName, e);
        }
    }
}

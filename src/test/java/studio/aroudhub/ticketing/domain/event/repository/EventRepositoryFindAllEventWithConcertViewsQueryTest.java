package studio.aroudhub.ticketing.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:event-with-concert-query-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRepositoryFindAllEventWithConcertViewsQueryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    void findAllEventWithConcertViews_returnsEmptyListWhenNoEventExists() {
        List<EventWithConcertResponse> result = eventRepository.findAllEventWithConcertViews();

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void findAllEventWithConcertViews_returnsProjectedDtoList() {
        Venue venue = new Venue();
        setField(venue, "name", "Olympic Hall");
        setField(venue, "address", "Seoul");
        entityManager.persist(venue);

        Concert concert = new Concert();
        setField(concert, "venue", venue);
        setField(concert, "title", "Summer Festival");
        setField(concert, "imgUrl", "https://cdn.example.com/concerts/101.png");
        setField(concert, "description", "Three day festival");
        setField(concert, "bookingCnt", 250);
        setField(concert, "createdAt", "2026-07-01");
        setField(concert, "runningTime", 120);
        setField(concert, "startDate", LocalDate.of(2026, 7, 20));
        setField(concert, "endDate", LocalDate.of(2026, 7, 22));
        setField(concert, "ageRating", "15+");
        entityManager.persist(concert);

        Event event = new Event();
        setField(event, "concert", concert);
        setField(event, "title", "Early bird discount");
        setField(event, "description", "Discount event for early reservations");
        setField(event, "status", "SCHEDULED");
        setField(event, "startDate", LocalDate.of(2026, 7, 5));
        setField(event, "endDate", LocalDate.of(2026, 7, 31));
        entityManager.persist(event);

        entityManager.flush();
        entityManager.clear();

        List<EventWithConcertResponse> result = eventRepository.findAllEventWithConcertViews();

        assertThat(result).hasSize(1);
        assertThat(result)
                .extracting(
                        EventWithConcertResponse::eventTitle,
                        EventWithConcertResponse::eventDesc,
                        EventWithConcertResponse::status,
                        EventWithConcertResponse::eventStartDate,
                        EventWithConcertResponse::eventEndDate
                )
                .containsExactly(tuple(
                        "Early bird discount",
                        "Discount event for early reservations",
                        "SCHEDULED",
                        LocalDate.of(2026, 7, 5),
                        LocalDate.of(2026, 7, 31)
                ));
        assertThat(result.get(0).concert().concertId()).isPositive();
        assertThat(result.get(0).concert().concertTitle()).isEqualTo("Summer Festival");
        assertThat(result.get(0).concert().imgUrl()).isEqualTo("https://cdn.example.com/concerts/101.png");
        assertThat(result.get(0).concert().concertDesc()).isEqualTo("Three day festival");
        assertThat(result.get(0).concert().venue()).isEqualTo("Olympic Hall");
        assertThat(result.get(0).concert().runningTime()).isEqualTo(120);
        assertThat(result.get(0).concert().concertStartDate()).isEqualTo(LocalDate.of(2026, 7, 20));
        assertThat(result.get(0).concert().concertEndDate()).isEqualTo(LocalDate.of(2026, 7, 22));
        assertThat(result.get(0).concert().ageRating()).isEqualTo("15+");
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to set field: " + fieldName, exception);
        }
    }
}

package studio.aroudhub.ticketing.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@SpringBootTest(
        classes = EventRepositoryFindAllEventWithConcertViewsQueryTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:event-with-concert-query-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.data.jpa.repositories.enabled=false"
})
class EventRepositoryFindAllEventWithConcertViewsQueryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void repositoryFindAllEventWithConcertViewsQuery_doesNotSucceedAndThrowsSemanticException() {
        assertThatThrownBy(() -> entityManager.createQuery("""
                select new studio.aroudhub.ticketing.domain.event.repository.EventWithConcertView(
                    e.eventId,
                    c.title,
                    e.description,
                    e.status,
                    e.startDate,
                    e.endDate,
                    c.concertId,
                    c.title,
                    c.imgUrl,
                    c.description,
                    c.bookingCnt,
                    c.createdAt,
                    v.name,
                    e.runningTime,
                    e.startDate,
                    e.endDate,
                    e.ageRating,
                    e.price,
                    e.date
                )
                from Event e
                join e.concert c
                join c.venue v
                """, EventWithConcertResponse.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not resolve class")
                .hasMessageContaining("EventWithConcertView");
    }

    @Test
    @Transactional
    void correctedFindAllEventWithConcertViewsQuery_returnsSelectResults() {
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
        setField(event, "status", "진행중");
        setField(event, "startDate", LocalDate.of(2026, 7, 5));
        setField(event, "endDate", LocalDate.of(2026, 7, 31));
        entityManager.persist(event);

        entityManager.flush();
        entityManager.clear();

        List<EventWithConcertResponse> result = entityManager.createQuery("""
                select new studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse(
                    e.eventId,
                    e.title,
                    e.description,
                    e.status,
                    e.startDate,
                    e.endDate,
                    c
                )
                from Event e
                join e.concert c
                join c.venue v
                """, EventWithConcertResponse.class).getResultList();

        System.out.println("findAllEventWithConcertViews success result = " + result);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).eventId()).isPositive();
        assertThat(result.get(0).title()).isEqualTo("Early bird discount");
        assertThat(result.get(0).description()).isEqualTo("Discount event for early reservations");
        assertThat(result.get(0).status()).isEqualTo("진행중");
        assertThat(result.get(0).startDate()).isEqualTo(LocalDate.of(2026, 7, 5));
        assertThat(result.get(0).endDate()).isEqualTo(LocalDate.of(2026, 7, 31));
        assertThat(result.get(0).concert().getTitle()).isEqualTo("Summer Festival");
        assertThat(result.get(0).concert().getVenue().getName()).isEqualTo("Olympic Hall");
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

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {
            Event.class,
            Concert.class,
            ConcertPrice.class,
            ConcertSchedule.class,
            Venue.class
    })
    static class TestApplication {
    }
}

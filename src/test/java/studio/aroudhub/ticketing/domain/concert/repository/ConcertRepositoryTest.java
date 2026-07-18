package studio.aroudhub.ticketing.domain.concert.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByConcertId_returnsConcertWhenItExists() {
        Venue venue = createVenue("Olympic Hall", "123 Seoul Street");
        entityManager.persist(venue);

        Concert concert = createConcert(
                venue,
                "Summer Jazz Festival",
                "https://cdn.example.com/summer-jazz.jpg",
                "Outdoor jazz concert",
                21,
                "2026-06-01",
                120,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 10),
                "12+"
        );
        entityManager.persist(concert);
        entityManager.flush();
        entityManager.clear();

        var result = concertRepository.findByConcertId(getIntField(concert, "concertId"));

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Summer Jazz Festival");
        assertThat(result.get().getVenue().getName()).isEqualTo("Olympic Hall");
    }

    @Test
    void findByConcertId_returnsEmptyWhenConcertDoesNotExist() {
        var result = concertRepository.findByConcertId(99999);

        assertThat(result).isEmpty();
    }

    @Test
    void findConcertPage_returnsConcertListResponsesOrderedByBookingCountDesc() {
        Venue venue = createVenue("Blue Square", "45 Gangnam-daero");
        entityManager.persist(venue);

        Concert lowerBookingConcert = createConcert(
                venue,
                "Late Night Rock",
                "https://cdn.example.com/late-night-rock.jpg",
                "Rock performance",
                11,
                "2026-06-02",
                130,
                LocalDate.of(2026, 8, 2),
                LocalDate.of(2026, 8, 2),
                "15+"
        );
        Concert higherBookingConcert = createConcert(
                venue,
                "Festival Headliner",
                "https://cdn.example.com/festival-headliner.jpg",
                "Main stage performance",
                25,
                "2026-06-03",
                150,
                LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 8, 6),
                "12+"
        );
        entityManager.persist(lowerBookingConcert);
        entityManager.persist(higherBookingConcert);
        entityManager.flush();
        entityManager.clear();

        Page<ConcertListResponse> result = concertRepository.findConcertPage(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).concertTitle()).isEqualTo("Festival Headliner");
        assertThat(result.getContent().get(0).bookingCnt()).isEqualTo(25);
        assertThat(result.getContent().get(0).venue()).isEqualTo("Blue Square");
        assertThat(result.getContent().get(1).concertTitle()).isEqualTo("Late Night Rock");
        assertThat(result.getContent().get(1).bookingCnt()).isEqualTo(11);
    }

    private Venue createVenue(String name, String address) {
        Venue venue = new Venue();
        setField(venue, "name", name);
        setField(venue, "address", address);
        return venue;
    }

    private Concert createConcert(
            Venue venue,
            String title,
            String imgUrl,
            String description,
            int bookingCnt,
            String createdAt,
            int runningTime,
            LocalDate startDate,
            LocalDate endDate,
            String ageRating
    ) {
        Concert concert = new Concert();
        setField(concert, "venue", venue);
        setField(concert, "title", title);
        setField(concert, "imgUrl", imgUrl);
        setField(concert, "description", description);
        setField(concert, "bookingCnt", bookingCnt);
        setField(concert, "createdAt", createdAt);
        setField(concert, "running_time", runningTime);
        setField(concert, "startDate", startDate);
        setField(concert, "endDate", endDate);
        setField(concert, "age_rating", ageRating);
        return concert;
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

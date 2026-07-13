package studio.aroudhub.ticketing.domain.event.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:event-api-integration-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void getEvents_returnsSampleJsonShapeThroughControllerServiceRepositoryFlow() throws Exception {
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

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(event.getEventId()))
                .andExpect(jsonPath("$[0].eventTitle").value("Early bird discount"))
                .andExpect(jsonPath("$[0].eventDesc").value("Discount event for early reservations"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[0].eventStartDate").value("2026-07-05"))
                .andExpect(jsonPath("$[0].eventEndDate").value("2026-07-31"))
                .andExpect(jsonPath("$[0].concert.concertId").value(concert.getConcertId()))
                .andExpect(jsonPath("$[0].concert.concertTitle").value("Summer Festival"))
                .andExpect(jsonPath("$[0].concert.imgUrl").value("https://cdn.example.com/concerts/101.png"))
                .andExpect(jsonPath("$[0].concert.concertDesc").value("Three day festival"))
                .andExpect(jsonPath("$[0].concert.venue").value("Olympic Hall"))
                .andExpect(jsonPath("$[0].concert.runningTime").value(120))
                .andExpect(jsonPath("$[0].concert.concertStartDate").value("2026-07-20"))
                .andExpect(jsonPath("$[0].concert.concertEndDate").value("2026-07-22"))
                .andExpect(jsonPath("$[0].concert.ageRating").value("15+"));
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

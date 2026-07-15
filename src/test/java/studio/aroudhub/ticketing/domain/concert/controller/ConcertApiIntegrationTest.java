package studio.aroudhub.ticketing.domain.concert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowList;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:concert-api-integration-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ConcertApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void getConcerts_returnsPagedConcertListThroughControllerServiceRepositoryFlow() throws Exception {
        Venue venue = createVenue("Blue Hall", "123 Seoul Street");
        entityManager.persist(venue);

        Concert concert = createConcert(
                venue,
                "Jazz at the River",
                "https://cdn.example.com/posters/jazz-river.jpg",
                "Outdoor jazz concert",
                87,
                "2026-07-01",
                110,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 7, 27),
                "12+"
        );
        entityManager.persist(concert);
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/concerts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].concertId").value(concert.getConcertId()))
                .andExpect(jsonPath("$.content[0].concertTitle").value("Jazz at the River"))
                .andExpect(jsonPath("$.content[0].imgUrl").value("https://cdn.example.com/posters/jazz-river.jpg"))
                .andExpect(jsonPath("$.content[0].bookingCnt").value(87))
                .andExpect(jsonPath("$.content[0].venue").value("Blue Hall"))
                .andExpect(jsonPath("$.content[0].startDate").value("2026-07-20"))
                .andExpect(jsonPath("$.content[0].endDate").value("2026-07-27"));
    }

    @Test
    @Transactional
    void getConcertDetail_returnsNestedSeatAndScheduleDataThroughControllerServiceRepositoryFlow() throws Exception {
        Venue venue = createVenue("Aurora Dome", "456 Busan Avenue");
        entityManager.persist(venue);

        Concert concert = createConcert(
                venue,
                "Midnight Synthwave",
                "https://cdn.example.com/posters/midnight-synthwave.jpg",
                "Arena synthwave show",
                548,
                "2026-07-02",
                135,
                LocalDate.of(2026, 8, 12),
                LocalDate.of(2026, 8, 15),
                "15+"
        );
        entityManager.persist(concert);

        ConcertPrice vipPrice = new ConcertPrice();
        setField(vipPrice, "concert", concert);
        setField(vipPrice, "rating", "VIP");
        setField(vipPrice, "price", 132000);
        entityManager.persist(vipPrice);

        ConcertSchedule concertSchedule = new ConcertSchedule();
        setField(concertSchedule, "concert", concert);
        setField(concertSchedule, "date", LocalDate.of(2026, 8, 12));
        setField(concertSchedule, "showList", List.of(createShowList(1, LocalTime.of(20, 0))));
        entityManager.persist(concertSchedule);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/concerts/{concertId}", concert.getConcertId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.concertId").value(concert.getConcertId()))
                .andExpect(jsonPath("$.concertTitle").value("Midnight Synthwave"))
                .andExpect(jsonPath("$.imgUrl").value("https://cdn.example.com/posters/midnight-synthwave.jpg"))
                .andExpect(jsonPath("$.bookingCnt").value(548))
                .andExpect(jsonPath("$.venue").value("Aurora Dome"))
                .andExpect(jsonPath("$.concertStartDate").value("2026-08-12"))
                .andExpect(jsonPath("$.concertEndDate").value("2026-08-15"))
                .andExpect(jsonPath("$.ageRating").value("15+"))
                .andExpect(jsonPath("$.seatList[0].rating").value("VIP"))
                .andExpect(jsonPath("$.seatList[0].price").value(132000))
                .andExpect(jsonPath("$.schedule[0].date").value("2026-08-12"))
                .andExpect(jsonPath("$.schedule[0].showList[0].showId").value(1))
                .andExpect(jsonPath("$.schedule[0].showList[0].time").value("20:00:00"));
    }

    @Test
    void getConcertDetail_whenConcertMissing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/concerts/{concertId}", 99999))
                .andExpect(status().isNotFound());
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
        setField(concert, "runningTime", runningTime);
        setField(concert, "startDate", startDate);
        setField(concert, "endDate", endDate);
        setField(concert, "ageRating", ageRating);
        return concert;
    }

    private ShowList createShowList(int id, LocalTime time) {
        try {
            Constructor<ShowList> constructor = ShowList.class.getDeclaredConstructor(int.class, LocalTime.class);
            constructor.setAccessible(true);
            return constructor.newInstance(id, time);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to create ShowList.", exception);
        }
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

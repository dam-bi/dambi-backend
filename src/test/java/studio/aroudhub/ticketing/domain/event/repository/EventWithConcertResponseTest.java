package studio.aroudhub.ticketing.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ScheduleShowTime;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

class EventWithConcertResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void writeValueAsString_serializesEventWithConcertResponseToJson() throws Exception {
        Concert concert = createConcertWithAllFields();
        EventWithConcertResponse response = new EventWithConcertResponse(
                1,
                "Early bird discount",
                "Discount event for early reservations",
                "진행중",
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 31),
                concert
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"eventId\":1");
        assertThat(json).contains("\"title\":\"Early bird discount\"");
        assertThat(json).contains("\"description\":\"Discount event for early reservations\"");
        assertThat(json).contains("\"status\":\"진행중\"");
        assertThat(json).contains("\"startDate\":\"2026-07-05\"");
        assertThat(json).contains("\"endDate\":\"2026-07-31\"");

        assertThat(json).contains("\"concertId\":101");
        assertThat(json).contains("\"title\":\"Summer Festival\"");
        assertThat(json).contains("\"imgUrl\":\"https://cdn.example.com/concerts/101.png\"");
        assertThat(json).contains("\"description\":\"Three day festival\"");
        assertThat(json).contains("\"bookingCnt\":250");
        assertThat(json).contains("\"createdAt\":\"2026-07-01T09:00:00\"");
        assertThat(json).contains("\"runningTime\":120");
        assertThat(json).contains("\"startDate\":\"2026-07-20\"");
        assertThat(json).contains("\"endDate\":\"2026-07-22\"");
        assertThat(json).contains("\"ageRating\":\"15+\"");

        // venue
        assertThat(json).contains("\"venue\":\"Olympic Hall\"");

        //price
        assertThat(json).contains("\"rating\":\"VIP\"");
        assertThat(json).contains("\"price\":220000");
        assertThat(json).contains("\"rating\":\"R\"");
        assertThat(json).contains("\"price\":150000");

        // date
        assertThat(json).contains("\"id\":801");
        assertThat(json).contains("\"date\":\"2026-07-20\"");
        assertThat(json).contains("\"showList\":[{\"id\":1,\"time\":\"14:00:00\"},{\"id\":2,\"time\":\"19:00:00\"}]");
        assertThat(json).contains("\"id\":802");
        assertThat(json).contains("\"date\":\"2026-07-21\"");
        assertThat(json).contains("\"showList\":[{\"id\":3,\"time\":\"15:00:00\"}]");
    }

    @Test
    void writeValueAsString_throwsWhenConcertContainsCycle() throws Exception {
        Concert concert = createConcertWithAllFields();
        ConcertPrice cyclicPrice = concert.getPrice().get(0);
        setField(cyclicPrice, "concert", concert);

        EventWithConcertResponse response = new EventWithConcertResponse(
                1,
                "Early bird discount",
                "Discount event for early reservations",
                "진행중",
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 31),
                concert
        );

        assertThrows(JsonMappingException.class, () -> objectMapper.writeValueAsString(response));
    }

    @Test
    void readValue_deserializesJsonToEventWithConcertResponse() throws Exception {
        String json = """
                {
                  "eventId": 1,
                  "title": "Early bird discount",
                  "description": "Discount event for early reservations",
                  "status": "진행중",
                  "startDate": "2026-07-05",
                  "endDate": "2026-07-31",
                  "concert": {
                    "concertId": 101,
                    "title": "Summer Festival",
                    "imgUrl": "https://cdn.example.com/concerts/101.png",
                    "description": "Three day festival",
                    "bookingCnt": 250,
                    "createdAt": "2026-07-01",
                    "venue": "Olympic Hall",
                    "runningTime": 120,
                    "startDate": "2026-07-20",
                    "endDate": "2026-07-22",
                    "ageRating": "15+",
                    "price": [
                      {
                        "rating": "VIP",
                        "price": 220000
                      },
                      {
                        "rating": "R",
                        "price": 150000
                      }
                    ],
                    "date": [
                      {
                        "id": 801,
                        "date": "2026-07-20",
                        "showList": [
                          {
                            "id": 1,
                            "time": "14:00"
                          },
                          {
                            "id": 2,
                            "time": "19:00:00"
                          }
                        ]
                      },
                      {
                        "id": 802,
                        "date": "2026-07-21",
                        "showList": [
                          {
                            "id": 3,
                            "time": "15:00"
                          }
                        ]
                      }
                    ]
                  }
                }
                """;

        EventWithConcertResponse response = objectMapper.readValue(json, EventWithConcertResponse.class);

        assertThat(response.eventId()).isEqualTo(1);
        assertThat(response.title()).isEqualTo("Early bird discount");
        assertThat(response.description()).isEqualTo("Discount event for early reservations");
        assertThat(response.status()).isEqualTo("진행중");
        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 7, 5));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 7, 31));

        Concert concert = response.concert();
        assertThat(concert).isNotNull();
        assertThat(concert.getConcertId()).isEqualTo(101);
        assertThat(concert.getTitle()).isEqualTo("Summer Festival");
        assertThat(concert.getImgUrl()).isEqualTo("https://cdn.example.com/concerts/101.png");
        assertThat(concert.getDescription()).isEqualTo("Three day festival");
        assertThat(concert.getBookingCnt()).isEqualTo(250);
        assertThat(concert.getCreatedAt()).isEqualTo("2026-07-01");
        assertThat(concert.getRunningTime()).isEqualTo(120);
        assertThat(concert.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 20));
        assertThat(concert.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 22));
        assertThat(concert.getAgeRating()).isEqualTo("15+");

        assertThat(concert.getVenue()).isNotNull();
        assertThat(concert.getVenue().getName()).isEqualTo("Olympic Hall");

        assertThat(concert.getPrice()).hasSize(2);
        assertThat(concert.getPrice().get(0).getRating()).isEqualTo("VIP");
        assertThat(concert.getPrice().get(0).getPrice()).isEqualTo(220000);
        assertThat(concert.getPrice().get(1).getRating()).isEqualTo("R");
        assertThat(concert.getPrice().get(1).getPrice()).isEqualTo(150000);

        assertThat(concert.getDate()).hasSize(2);
        assertThat(concert.getDate().get(0).getDate()).isEqualTo(LocalDate.of(2026, 7, 20));
        assertThat(concert.getDate().get(0).getShowList()).containsExactly(
                new ScheduleShowTime(1, LocalTime.of(14, 0)),
                new ScheduleShowTime(2, LocalTime.of(19, 0))
        );
        assertThat(concert.getDate().get(1).getDate()).isEqualTo(LocalDate.of(2026, 7, 21));
        assertThat(concert.getDate().get(1).getShowList()).containsExactly(
                new ScheduleShowTime(3, LocalTime.of(15, 0))
        );
    }

    @Test
    void readValue_throwsWhenJsonContainsInvalidFieldType() {
        String json = """
                {
                  "eventId": 1,
                  "title": "Early bird discount",
                  "description": "Discount event for early reservations",
                  "status": "진행중",
                  "startDate": "11111-8915-78651",
                  "endDate": "2026-07-31",
                  "concert": {
                    "concertId": 101,
                    "title": "Summer Festival"
                  }
                }
                """;

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, EventWithConcertResponse.class));
    }

    @Test
    void readValue_throwsWhenJsonContainsInvalidFieldTypeArray() {
        String json = """
                {
                  "eventId": 1,
                  "title": "Early bird discount",
                  "description": "Discount event for early reservations",
                  "status": "진행중",
                  "startDate": "11111-8915-78651",
                  "endDate": "2026-07-31",
                  "concert": {
                    "concertId": 101,
                  }
                }
                """;

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, EventWithConcertResponse.class));
    }

    private Concert createConcertWithAllFields() throws Exception {
        Concert concert = instantiate(Concert.class);
        Venue venue = instantiate(Venue.class);
        ConcertPrice vipPrice = instantiate(ConcertPrice.class);
        ConcertPrice regularPrice = instantiate(ConcertPrice.class);
        ConcertSchedule firstSchedule = instantiate(ConcertSchedule.class);
        ConcertSchedule secondSchedule = instantiate(ConcertSchedule.class);

        setField(venue, "venueId", 12);
        setField(venue, "name", "Olympic Hall");
        setField(venue, "address", "Seoul");

        setField(vipPrice, "concertPriceId", 501);
        setField(vipPrice, "rating", "VIP");
        setField(vipPrice, "price", 220000);

        setField(regularPrice, "concertPriceId", 502);
        setField(regularPrice, "rating", "R");
        setField(regularPrice, "price", 150000);

        setField(firstSchedule, "concertScheduleId", 801);
        setField(firstSchedule, "date", LocalDate.of(2026, 7, 20));
        setField(firstSchedule, "showList", List.of(
                new ScheduleShowTime(1, LocalTime.of(14, 0)),
                new ScheduleShowTime(2, LocalTime.of(19, 0))
        ));

        setField(secondSchedule, "concertScheduleId", 802);
        setField(secondSchedule, "date", LocalDate.of(2026, 7, 21));
        setField(secondSchedule, "showList", List.of(
                new ScheduleShowTime(3, LocalTime.of(15, 0))
        ));

        setField(concert, "concertId", 101);
        setField(concert, "venue", venue);
        setField(concert, "title", "Summer Festival");
        setField(concert, "imgUrl", "https://cdn.example.com/concerts/101.png");
        setField(concert, "description", "Three day festival");
        setField(concert, "bookingCnt", 250);
        setField(concert, "createdAt", "2026-07-01T09:00:00");
        setField(concert, "runningTime", 120);
        setField(concert, "startDate", LocalDate.of(2026, 7, 20));
        setField(concert, "endDate", LocalDate.of(2026, 7, 22));
        setField(concert, "ageRating", "15+");
        setField(concert, "price", List.of(vipPrice, regularPrice));
        setField(concert, "date", List.of(firstSchedule, secondSchedule));

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

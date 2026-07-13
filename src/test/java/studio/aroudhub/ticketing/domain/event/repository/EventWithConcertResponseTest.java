package studio.aroudhub.ticketing.domain.event.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EventWithConcertResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void writeValueAsString_serializesEventWithConcertResponseToJson() throws Exception {
        EventWithConcertResponse response = createResponse();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"eventId\":1");
        assertThat(json).contains("\"eventTitle\":\"Early bird discount\"");
        assertThat(json).contains("\"eventDesc\":\"Discount event for early reservations\"");
        assertThat(json).contains("\"status\":\"SCHEDULED\"");
        assertThat(json).contains("\"eventStartDate\":\"2026-07-05\"");
        assertThat(json).contains("\"eventEndDate\":\"2026-07-31\"");
        assertThat(json).contains("\"concertId\":101");
        assertThat(json).contains("\"concertTitle\":\"Summer Festival\"");
        assertThat(json).contains("\"concertDesc\":\"Three day festival\"");
        assertThat(json).contains("\"venue\":\"Olympic Hall\"");
        assertThat(json).contains("\"runningTime\":120");
        assertThat(json).contains("\"concertStartDate\":\"2026-07-20\"");
        assertThat(json).contains("\"concertEndDate\":\"2026-07-22\"");
        assertThat(json).contains("\"ageRating\":\"15+\"");
    }

    @Test
    void readValue_deserializesJsonToEventWithConcertResponse() throws Exception {
        String json = """
                {
                  "eventId": 1,
                  "eventTitle": "Early bird discount",
                  "eventDesc": "Discount event for early reservations",
                  "status": "SCHEDULED",
                  "eventStartDate": "2026-07-05",
                  "eventEndDate": "2026-07-31",
                  "concert": {
                    "concertId": 101,
                    "concertTitle": "Summer Festival",
                    "imgUrl": "https://cdn.example.com/concerts/101.png",
                    "concertDesc": "Three day festival",
                    "venue": "Olympic Hall",
                    "runningTime": 120,
                    "concertStartDate": "2026-07-20",
                    "concertEndDate": "2026-07-22",
                    "ageRating": "15+"
                  }
                }
                """;

        EventWithConcertResponse response = objectMapper.readValue(json, EventWithConcertResponse.class);

        assertThat(response.eventId()).isEqualTo(1);
        assertThat(response.eventTitle()).isEqualTo("Early bird discount");
        assertThat(response.eventDesc()).isEqualTo("Discount event for early reservations");
        assertThat(response.status()).isEqualTo("SCHEDULED");
        assertThat(response.eventStartDate()).isEqualTo(LocalDate.of(2026, 7, 5));
        assertThat(response.eventEndDate()).isEqualTo(LocalDate.of(2026, 7, 31));
        assertThat(response.concert().concertId()).isEqualTo(101);
        assertThat(response.concert().concertTitle()).isEqualTo("Summer Festival");
        assertThat(response.concert().concertDesc()).isEqualTo("Three day festival");
        assertThat(response.concert().venue()).isEqualTo("Olympic Hall");
    }

    @Test
    void readValue_throwsWhenJsonContainsInvalidFieldType() {
        String json = """
                {
                  "eventId": 1,
                  "eventTitle": "Early bird discount",
                  "eventDesc": "Discount event for early reservations",
                  "status": "SCHEDULED",
                  "eventStartDate": "invalid-date",
                  "eventEndDate": "2026-07-31",
                  "concert": null
                }
                """;

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, EventWithConcertResponse.class));
    }

    private EventWithConcertResponse createResponse() {
        return new EventWithConcertResponse(
                1,
                "Early bird discount",
                "Discount event for early reservations",
                "SCHEDULED",
                LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 31),
                new ConcertResponse(
                        101,
                        "Summer Festival",
                        "https://cdn.example.com/concerts/101.png",
                        "Three day festival",
                        "Olympic Hall",
                        120,
                        LocalDate.of(2026, 7, 20),
                        LocalDate.of(2026, 7, 22),
                        "15+"
                )
        );
    }
}

package studio.aroudhub.ticketing.domain.concert.repository.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.concert.repository.ScheduleShowListConverter;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcertConvertersTest {

    private final ScheduleShowListConverter scheduleShowListConverter = new ScheduleShowListConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void scheduleShowListConverter_serializesScheduleShowTimesToJsonArray() throws Exception {
        List<ShowList> showTimes = List.of(
                new ShowList(1, LocalTime.of(13, 0)),
                new ShowList(2, LocalTime.of(18, 0))
        );

        String json = scheduleShowListConverter.convertToDatabaseColumn(showTimes);
        List<?> jsonArray = objectMapper.readValue(json, new TypeReference<List<?>>() {});

        assertEquals(
                "[{\"id\":1,\"time\":\"13:00\"},{\"id\":2,\"time\":\"18:00\"}]",
                json
        );
        assertEquals(2, jsonArray.size());
    }

    @Test
    void scheduleShowListConverter_deserializesJsonArrayToScheduleShowTimes() {
        String json = """
                [
                  {"id":1,"time":"13:00"},
                  {"id":2,"time":"20:00"}
                ]
                """;

        List<ShowList> result = scheduleShowListConverter.convertToEntityAttribute(json);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(LocalTime.of(13, 0), result.get(0).getTime());
        assertEquals(2, result.get(1).getId());
        assertEquals(LocalTime.of(20, 0), result.get(1).getTime());
    }

    @Test
    void scheduleShowListConverter_deserializesToScheduleShowTimeListType() {
        String json = """
                [
                  {"id":1,"time":"13:00"}
                ]
                """;

        List<ShowList> result = scheduleShowListConverter.convertToEntityAttribute(json);

        assertEquals(ShowList.class, result.get(0).getClass());
    }

    @Test
    void scheduleShowListConverter_returnsEmptyListForNullJson() {
        List<ShowList> result = scheduleShowListConverter.convertToEntityAttribute(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void scheduleShowListConverter_returnsEmptyListForBlankJson() {
        List<ShowList> result = scheduleShowListConverter.convertToEntityAttribute("   ");

        assertTrue(result.isEmpty());
    }
}

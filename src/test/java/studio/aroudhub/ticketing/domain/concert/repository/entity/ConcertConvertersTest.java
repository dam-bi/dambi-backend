package studio.aroudhub.ticketing.domain.concert.repository.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcertConvertersTest {

    private final ScheduleShowListConverter scheduleShowListConverter = new ScheduleShowListConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void scheduleShowListConverter_serializesScheduleShowTimesToJsonArray() throws Exception {
        List<ScheduleShowTime> showTimes = List.of(
                new ScheduleShowTime(1, LocalTime.of(13, 0)),
                new ScheduleShowTime(2, LocalTime.of(18, 0))
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

        List<ScheduleShowTime> result = scheduleShowListConverter.convertToEntityAttribute(json);

        assertEquals(
                List.of(
                        new ScheduleShowTime(1, LocalTime.of(13, 0)),
                        new ScheduleShowTime(2, LocalTime.of(20, 0))
                ),
                result
        );
    }

    @Test
    void scheduleShowListConverter_deserializesToScheduleShowTimeListType() {
        String json = """
                [
                  {"id":1,"time":"13:00"}
                ]
                """;

        List<ScheduleShowTime> result = scheduleShowListConverter.convertToEntityAttribute(json);

        assertEquals(ScheduleShowTime.class, result.get(0).getClass());
    }

    @Test
    void scheduleShowListConverter_returnsEmptyListForNullJson() {
        List<ScheduleShowTime> result = scheduleShowListConverter.convertToEntityAttribute(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void scheduleShowListConverter_returnsEmptyListForBlankJson() {
        List<ScheduleShowTime> result = scheduleShowListConverter.convertToEntityAttribute("   ");

        assertTrue(result.isEmpty());
    }
}

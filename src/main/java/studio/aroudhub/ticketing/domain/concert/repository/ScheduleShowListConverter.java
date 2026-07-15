package studio.aroudhub.ticketing.domain.concert.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowList;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Converter
public class ScheduleShowListConverter implements AttributeConverter<List<ShowList>, String> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
    private static ObjectMapper createObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(javaTimeModule)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(List<ShowList> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize schedule showList.", e);
        }
    }

    @Override
    public List<ShowList> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(dbData);
            if (root.isTextual()) {
                root = OBJECT_MAPPER.readTree(root.asText());
            }

            List<ShowList> showTimes = new ArrayList<>();
            for (JsonNode showTime : root) {
                showTimes.add(new ShowList(
                        showTime.get("id").asInt(),
                        parseTime(showTime.get("time").asText())
                ));
            }
            return showTimes;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize schedule showList.", e);
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return LocalTime.parse(value);
        }
    }
}

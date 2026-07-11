package studio.aroudhub.ticketing.domain.concert.repository.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class ShowInfoListConverter implements AttributeConverter<List<ShowInfo>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final TypeReference<List<ShowInfo>> SHOW_INFO_LIST_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<ShowInfo> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize showList.", e);
        }
    }

    @Override
    public List<ShowInfo> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, SHOW_INFO_LIST_TYPE);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize showList.", e);
        }
    }
}

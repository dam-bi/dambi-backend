package studio.aroudhub.ticketing.domain.venue.repository.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.lang.reflect.Field;

public class VenueJsonDeserializer extends JsonDeserializer<Venue> {

    @Override
    public Venue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return createVenueFromName(parser.getValueAsString());
        }

        JsonNode node = parser.getCodec().readTree(parser);
        Venue venue = new Venue();
        setField(venue, "venueId", node.path("venueId").asInt(0));
        setField(venue, "name", node.path("name").asText(null));
        setField(venue, "address", node.path("address").asText(null));
        return venue;
    }

    private Venue createVenueFromName(String name) throws IOException {
        Venue venue = new Venue();
        setField(venue, "name", name);
        return venue;
    }

    private void setField(Venue venue, String fieldName, Object value) throws IOException {
        try {
            Field field = Venue.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(venue, value);
        } catch (ReflectiveOperationException exception) {
            throw new IOException("Failed to deserialize Venue.", exception);
        }
    }
}

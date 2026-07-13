package studio.aroudhub.ticketing.domain.venue.repository.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class VenueJsonSerializer extends JsonSerializer<Venue> {

    @Override
    public void serialize(Venue value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeString(value.getName());
    }
}

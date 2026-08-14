package studio.aroudhub.ticketing.domain.event.repository.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

class EventEntityStructureTest {

    @Test
    void event_usesExpectedTableName() {
        Table table = Event.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("event", table.name());
    }

    @Test
    void event_hasConcertOneToOneAssociation() throws NoSuchFieldException {
        Field concertField = Event.class.getDeclaredField("concert");
        OneToOne oneToOne = concertField.getAnnotation(OneToOne.class);
        JoinColumn joinColumn = concertField.getAnnotation(JoinColumn.class);

        assertEquals(Concert.class, concertField.getType());
        assertNotNull(oneToOne);
        assertNotNull(joinColumn);
        assertEquals("concert_id", joinColumn.name());
    }

    @Test
    void event_usesExpectedSnakeCaseColumns() throws NoSuchFieldException {
        assertColumnName("eventId", "event_id");
        assertColumnName("createdAt", "created_at");
        assertColumnName("startDate", "start_date");
        assertColumnName("endDate", "end_date");
        assertColumnName("status", "status");
    }

    private static void assertColumnName(String fieldName, String expectedColumnName) throws NoSuchFieldException {
        Field field = Event.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertNotNull(column);
        assertEquals(expectedColumnName, column.name());
    }
}

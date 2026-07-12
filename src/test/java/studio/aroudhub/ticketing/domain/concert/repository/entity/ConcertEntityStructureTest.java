package studio.aroudhub.ticketing.domain.concert.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcertEntityStructureTest {

    @Test
    void concert_usesExpectedTableName() {
        // Concert 엔티티가 실제 DB의 concert 테이블에 매핑되는지 확인한다.
        Table table = Concert.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("concert", table.name());
    }

    @Test
    void concert_hasVenueManyToOneAssociation() throws NoSuchFieldException {
        // 공연은 하나의 Venue를 참조해야 하므로 venue 필드의 연관관계와 조인 컬럼을 확인한다.
        Field venueField = Concert.class.getDeclaredField("venue");
        ManyToOne manyToOne = venueField.getAnnotation(ManyToOne.class);
        JoinColumn joinColumn = venueField.getAnnotation(JoinColumn.class);

        assertEquals(Venue.class, venueField.getType());
        assertNotNull(manyToOne);
        assertNotNull(joinColumn);
        assertEquals("venue_id", joinColumn.name());
    }

    @Test
    void concert_hasPriceOneToManyAssociation() throws NoSuchFieldException {
        // 공연 상세의 price 필드가 ConcertPrice 목록으로 연결되는지 확인한다.
        Field priceField = Concert.class.getDeclaredField("price");
        OneToMany oneToMany = priceField.getAnnotation(OneToMany.class);

        assertEquals(List.class, priceField.getType());
        assertEquals(ConcertPrice.class, getListElementType(priceField));
        assertNotNull(oneToMany);
        assertEquals("concert", oneToMany.mappedBy());
    }

    @Test
    void concert_hasDateOneToManyAssociation() throws NoSuchFieldException {
        // 공연 상세의 date 필드가 ConcertSchedule 목록으로 연결되는지 확인한다.
        Field dateField = Concert.class.getDeclaredField("date");
        OneToMany oneToMany = dateField.getAnnotation(OneToMany.class);

        assertEquals(List.class, dateField.getType());
        assertEquals(ConcertSchedule.class, getListElementType(dateField));
        assertNotNull(oneToMany);
        assertEquals("concert", oneToMany.mappedBy());
    }

    @Test
    void concert_usesExpectedSnakeCaseColumns() throws NoSuchFieldException {
        // 스네이크 케이스 컬럼명이 엔티티 필드에 정확히 매핑되어 있는지 확인한다.
        assertColumnName("concertId", "concert_id");
        assertColumnName("imgUrl", "img_url");
        assertColumnName("bookingCnt", "booking_cnt");
        assertColumnName("createdAt", "created_at");
        assertColumnName("age_rating", "age_rating");
    }

    private static void assertColumnName(String fieldName, String expectedColumnName) throws NoSuchFieldException {
        Field field = Concert.class.getDeclaredField(fieldName);
        Column column = field.getAnnotation(Column.class);

        assertNotNull(column);
        assertEquals(expectedColumnName, column.name());
    }

    private static Class<?> getListElementType(Field field) {
        Type genericType = field.getGenericType();
        assertTrue(genericType instanceof ParameterizedType);
        Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        assertTrue(actualType instanceof Class<?>);
        return (Class<?>) actualType;
    }
}

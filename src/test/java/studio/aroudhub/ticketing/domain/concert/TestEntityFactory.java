package studio.aroudhub.ticketing.domain.concert;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertPrice;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ConcertSchedule;
import studio.aroudhub.ticketing.domain.concert.repository.entity.ShowList;

public final class TestEntityFactory {

    private TestEntityFactory() {
    }

    public static ConcertPrice createConcertPrice(int concertPriceId, String rating, int price) {
        ConcertPrice concertPrice = new ConcertPrice();
        setField(concertPrice, "concertPriceId", concertPriceId);
        setField(concertPrice, "rating", rating);
        setField(concertPrice, "price", price);
        return concertPrice;
    }

    public static ConcertSchedule createConcertSchedule(
            int concertScheduleId,
            LocalDate date,
            List<ShowList> showList
    ) {
        ConcertSchedule concertSchedule = new ConcertSchedule();
        setField(concertSchedule, "concertScheduleId", concertScheduleId);
        setField(concertSchedule, "date", date);
        setField(concertSchedule, "showList", showList);
        return concertSchedule;
    }

    public static ShowList createShowList(int id, LocalTime time) {
        try {
            Constructor<ShowList> constructor = ShowList.class.getDeclaredConstructor(int.class, LocalTime.class);
            constructor.setAccessible(true);
            return constructor.newInstance(id, time);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create test ShowList.", e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to set test field: " + fieldName, e);
        }
    }
}

package studio.aroudhub.ticketing.domain.event.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventList(
                e.eventId,
                c.concertId,
                e.title,
                e.description, 
                e.status
            )
            from Event e
            join e.concert c
            """)
    List<EventList> findAllEventLists();

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventWithConcertView(
                e.eventId,
                c.title,
                e.description,
                e.status,
                e.startDate,
                e.endDate,
                c.concertId,
                c.title,
                c.imgUrl,
                c.description,
                c.bookingCnt,
                c.createdAt,
                v.name,
                e.runningTime,
                e.startDate,
                e.endDate,
                e.ageRating,
                e.price,
                e.date
            )
            from Event e
            join e.concert c
            join c.venue v
            """)
    List<EventWithConcertResponse> findAllEventWithConcertViews();
}

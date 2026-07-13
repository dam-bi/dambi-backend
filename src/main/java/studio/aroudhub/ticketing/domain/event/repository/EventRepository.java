package studio.aroudhub.ticketing.domain.event.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventList(
                e.eventId,
                e.concert,
                e.title,
                e.description,
                e.startDate,
                e.endDate,
                e.status
            )
            from Event e
            join e.concert c
            """)
    List<EventList> findAllEventLists();

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventWithConcertResponse(
                e.eventId,
                e.title,
                e.description,
                e.status,
                e.startDate,
                e.endDate,
                new studio.aroudhub.ticketing.domain.event.repository.ConcertResponse(
                    c.concertId,
                    c.title,
                    c.imgUrl,
                    c.description,
                    v.name,
                    c.runningTime,
                    c.startDate,
                    c.endDate,
                    c.ageRating
                )
            )
            from Event e
            join e.concert c
            join c.venue v
            """)
    List<EventWithConcertResponse> findAllEventWithConcertViews();
}

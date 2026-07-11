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
                e.description
            )
            from Event e
            join e.concert c
            """)
    List<EventList> findAllEventLists();
}

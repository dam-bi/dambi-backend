package studio.aroudhub.ticketing.domain.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventListResponse(
                e.eventId,
                e.title,
                c.imgUrl,
                e.status,
                e.startDate,
                e.endDate
            )
            from Event e
            join e.concert c
            order by e.startDate asc, e.eventId asc
            """)
    List<EventListResponse> findEventList();

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventListResponse(
                e.eventId,
                e.title,
                c.imgUrl,
                e.status,
                e.startDate,
                e.endDate
            )
            from Event e
            join e.concert c
            order by e.startDate asc, e.eventId asc
            """)
    Page<EventListResponse> findEventPage(Pageable pageable);

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.EventDetailResponse(
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
            where e.eventId = :eventID
            """)
    Optional<EventDetailResponse> findEventDetailByEventId(int eventID);
}

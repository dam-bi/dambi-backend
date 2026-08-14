package studio.aroudhub.ticketing.domain.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventDetailResponse;
import studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse(
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
            select new studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventListResponse(
                e.eventId,
                e.title,
                c.imgUrl,
                e.status,
                e.startDate,
                e.endDate
            )
            from Event e
            join e.concert c
            where :status is null or e.status = :status
            """)
    Page<EventListResponse> findEventPage(Pageable pageable,  @Param("status") String status);

    @Query("""
            select new studio.aroudhub.ticketing.domain.event.repository.DTO.response.EventDetailResponse(
                e.eventId,
                e.title,
                e.description,
                e.status,
                e.startDate,
                e.endDate,
                new studio.aroudhub.ticketing.domain.event.repository.DTO.response.ConcertResponse(
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

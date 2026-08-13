package studio.aroudhub.ticketing.domain.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse(
                        c.concertId, c.title, c.imgUrl, c.bookingCnt,
                        c.venue.name, c.startDate, c.endDate)
            from Concert c
            order by c.bookingCnt desc, c.concertId asc
            """)
    Page<ConcertListResponse> findConcertPageOrderByBookingCntDesc(Pageable pageable);

    @Query("""
            select new studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse(
                        c.concertId, c.title, c.imgUrl, c.bookingCnt,
                        c.venue.name, c.startDate, c.endDate)
            from Concert c
            left join c.price cp
            group by c.concertId, c.title, c.imgUrl, c.bookingCnt, c.venue.name, c.startDate, c.endDate
            order by max(cp.price) desc, c.concertId asc
            """)
    Page<ConcertListResponse> findConcertPageOrderByHighestPriceDesc(Pageable pageable);

    @Query("""
            select new studio.aroudhub.ticketing.domain.concert.repository.DTO.response.ConcertListResponse(
                        c.concertId, c.title, c.imgUrl, c.bookingCnt,
                        c.venue.name, c.startDate, c.endDate)
            from Concert c
            left join c.price cp
            group by c.concertId, c.title, c.imgUrl, c.bookingCnt, c.venue.name, c.startDate, c.endDate
            order by min(cp.price) asc, c.concertId asc
            """)
    Page<ConcertListResponse> findConcertPageOrderByLowestPriceAsc(Pageable pageable);

    @EntityGraph(attributePaths = "venue")
    java.util.Optional<Concert> findByConcertId(int concertId);
}

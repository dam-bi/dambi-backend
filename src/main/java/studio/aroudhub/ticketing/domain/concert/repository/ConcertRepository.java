package studio.aroudhub.ticketing.domain.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Integer> {

    @Query("""
            select new studio.aroudhub.ticketing.domain.concert.repository.ConcertListItem(
                c.concertId,
                c.title,
                c.imgUrl,
                v.name,
                c.startDate,
                c.endDate,
                c.price
            )
            from Concert c
            join c.venue v
            order by c.startDate asc, c.concertId asc
            """)
    Page<ConcertListItem> findConcertPage(Pageable pageable);

    @EntityGraph(attributePaths = "venue")
    java.util.Optional<Concert> findByConcertId(int concertId);
}

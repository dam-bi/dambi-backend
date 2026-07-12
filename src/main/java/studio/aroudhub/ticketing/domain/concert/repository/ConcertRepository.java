package studio.aroudhub.ticketing.domain.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

public interface ConcertRepository extends JpaRepository<Concert, Integer> {

    @EntityGraph(attributePaths = {"venue", "price"})
    @Query("""
            select c
            from Concert c
            """)
    Page<Concert> findConcertPage(Pageable pageable);

    @EntityGraph(attributePaths = "venue")
    java.util.Optional<Concert> findByConcertId(int concertId);
}

package studio.aroudhub.ticketing.domain.venue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
}

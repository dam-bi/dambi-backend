package studio.aroudhub.ticketing.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.aroudhub.ticketing.domain.event.repository.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}

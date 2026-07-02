package studio.aroudhub.ticketing.domain.event.repository.entity;

import jakarta.persistence.*;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

}

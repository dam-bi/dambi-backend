package studio.aroudhub.ticketing.domain.event.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import studio.aroudhub.ticketing.domain.concert.repository.entity.Concert;

@Entity
@Getter
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    // status: 예정, 진행중, 종료
    @Column(name = "status", nullable = false, length = 255)
    private String status;
}

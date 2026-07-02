package studio.aroudhub.ticketing.domain.concert.repository.entity;

import jakarta.persistence.*;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "booking_cnt", nullable = false)
    private int bookingCnt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}

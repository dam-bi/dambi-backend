package studio.aroudhub.ticketing.domain.concert.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private int concertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
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

    // concert List에서는 venue : 장소도 추가해야 하지만, 여기서는 생략. concertListDTO에서 추가예정.

    @Column(name="running_time")
    private int running_time;

    @Column(name="start_date", nullable = false)
    private LocalDateTime startDate;
}

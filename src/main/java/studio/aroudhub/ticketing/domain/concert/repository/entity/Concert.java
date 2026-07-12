package studio.aroudhub.ticketing.domain.concert.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "booking_cnt", nullable = false)
    private int bookingCnt;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "running_time")
    private int running_time;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "age_rating", nullable = false)
    private String age_rating;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY)
    private List<ConcertPrice> price;

//    @Convert(converter = ShowInfoListConverter.class)
//    @Column(name = "show_list", columnDefinition = "json", nullable = false)
//    private List<ShowInfo> showList;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY)
    private List<ConcertSchedule> date;
}

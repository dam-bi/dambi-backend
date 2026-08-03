package studio.aroudhub.ticketing.domain.concert.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
import java.util.List;

@Entity
@Getter
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    @JsonProperty("concertId")
    private int concertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    @JsonProperty("venue")
    private Venue venue;

    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    private String title;

    @Column(name = "img_url", length = 255)
    @JsonProperty("imgUrl")
    private String imgUrl;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "booking_cnt", nullable = false)
    @JsonProperty("bookingCnt")
    private int bookingCnt;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("createdAt")
    private LocalDate createdAt;

    @Column(name = "running_time")
    @JsonProperty("runningTime")
    private int runningTime;

    @Column(name = "start_date", nullable = false)
    @JsonProperty("startDate")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @JsonProperty("endDate")
    private LocalDate endDate;

    @Column(name = "age_rating", nullable = false)
    @JsonProperty("ageRating")
    private String ageRating;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY)
    @JsonProperty("price")
    private List<ConcertPrice> price;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY)
    @JsonProperty("date")
    private List<ConcertSchedule> date;
}

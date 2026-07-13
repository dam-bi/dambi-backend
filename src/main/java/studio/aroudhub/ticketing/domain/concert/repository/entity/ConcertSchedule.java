package studio.aroudhub.ticketing.domain.concert.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import studio.aroudhub.ticketing.domain.concert.repository.ScheduleShowListConverter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(name = "concert_schedule")
public class ConcertSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_schedule_id")
    // db.json에서 id로 표현.
    @JsonProperty("id")
    private int concertScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "date", nullable = false)
    @JsonProperty("date")
    private LocalDate date;

    @Convert(converter = ScheduleShowListConverter.class)
    @Column(name = "show_list", columnDefinition = "json", nullable = false)
    @JsonProperty("showList")
    private List<ScheduleShowTime> showList;
}

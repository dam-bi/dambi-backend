package studio.aroudhub.ticketing.domain.seats.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import studio.aroudhub.ticketing.domain.venue.repository.entity.Venue;

@Entity
@Getter
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private int seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "section", nullable = false)
    private String section;

    @Column(name = "row_name", nullable = false)
    private String rowName;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
}

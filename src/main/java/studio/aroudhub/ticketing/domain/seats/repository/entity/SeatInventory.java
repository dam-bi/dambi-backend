package studio.aroudhub.ticketing.domain.seats.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "seat_inventory")
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_inventory_id")
    private int seatInventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    /*
    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "price", nullable = false)
    private int price;
     */

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatInventoryStatus status;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

}

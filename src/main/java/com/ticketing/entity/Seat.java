package com.ticketing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "seat_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Seat number is required")
    @Column(nullable = false)
    private String seatNumber;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatCategory category;

    @NotNull(message = "Availability status is required")
    @Column(nullable = false)
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToOne(mappedBy = "seat")
    private Ticket ticket;

    public enum SeatCategory {
        VIP(1.5),
        STANDARD(1.0),
        ECONOMY(0.7);

        private final double priceMultiplier;

        SeatCategory(double priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
        }

        public double getPriceMultiplier() {
            return priceMultiplier;
        }
    }

}

package com.ticketing.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Age group is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeGroup ageGroup = AgeGroup.ADULT;

    @NotNull(message = "Discount percentage is required")
    @Column(nullable = false)
    private Double discountPercentage = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"ticket", "event"})
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"tickets"})
    private Order order;

    public enum AgeGroup {
        CHILD(0.5),
        STUDENT(0.8),
        ADULT(1.0),
        SENIOR(0.7);

        private final double discountMultiplier;

        AgeGroup(double discountMultiplier) {
            this.discountMultiplier = discountMultiplier;
        }

        public double getDiscountMultiplier() {
            return discountMultiplier;
        }
    }

}

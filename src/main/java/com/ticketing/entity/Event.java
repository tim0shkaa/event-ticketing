package com.ticketing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Event description is required")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Event date is required")
    @Column(nullable = false)
    private LocalDateTime eventDate;

    @NotBlank(message = "Venue is required")
    @Column(nullable = false)
    private String venue;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    @Column(nullable = false)
    private Double basePrice;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

}

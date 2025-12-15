package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String venue;
    private Double basePrice;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<SeatResponse> seats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatResponse {
        private Long id;
        private String seatNumber;
        private String category;
        private Boolean available;
        private Double price;
    }

}

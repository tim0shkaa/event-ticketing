package com.ticketing.dto;

import com.ticketing.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String customerName;
    private String email;
    private LocalDateTime orderDate;
    private Double totalPrice;
    private Order.OrderStatus status;
    private Long eventId;
    private String eventName;
    private List<TicketResponse> tickets;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketResponse {
        private Long id;
        private String seatNumber;
        private Double price;
        private String ageGroup;
        private Double discountPercentage;
    }

}

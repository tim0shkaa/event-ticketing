package com.ticketing.dto;

import com.ticketing.entity.Ticket;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "At least one ticket is required")
    private List<TicketRequest> tickets;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketRequest {

        @NotNull(message = "Seat ID is required")
        private Long seatId;

        @NotNull(message = "Age group is required")
        private Ticket.AgeGroup ageGroup;

    }

}

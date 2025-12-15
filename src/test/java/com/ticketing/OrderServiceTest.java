package com.ticketing;

import com.ticketing.dto.OrderRequest;
import com.ticketing.entity.*;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.exception.SeatNotAvailableException;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.OrderRepository;
import com.ticketing.repository.SeatRepository;
import com.ticketing.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_Success() {
        Event event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setEventDate(LocalDateTime.now().plusDays(20));
        event.setBasePrice(100.0);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setSeatNumber("A1");
        seat.setCategory(Seat.SeatCategory.STANDARD);
        seat.setAvailable(true);
        seat.setEvent(event);

        OrderRequest.TicketRequest ticketRequest = new OrderRequest.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setAgeGroup(Ticket.AgeGroup.ADULT);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setEventId(1L);
        orderRequest.setCustomerName("John Doe");
        orderRequest.setEmail("john@example.com");
        orderRequest.setTickets(List.of(ticketRequest));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomerName("John Doe");
        savedOrder.setEmail("john@example.com");
        savedOrder.setEvent(event);
        savedOrder.setOrderDate(LocalDateTime.now());
        savedOrder.setTotalPrice(90.0);
        savedOrder.setStatus(Order.OrderStatus.CONFIRMED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        var result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        verify(eventRepository, times(1)).findById(1L);
        verify(seatRepository, times(1)).findById(1L);
    }

    @Test
    void createOrder_EventNotFound() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setEventId(999L);
        orderRequest.setCustomerName("John Doe");
        orderRequest.setEmail("john@example.com");

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });
    }

    @Test
    void createOrder_SeatNotAvailable() {
        Event event = new Event();
        event.setId(1L);
        event.setEventDate(LocalDateTime.now().plusDays(20));
        event.setBasePrice(100.0);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setSeatNumber("A1");
        seat.setCategory(Seat.SeatCategory.STANDARD);
        seat.setAvailable(false);
        seat.setEvent(event);

        OrderRequest.TicketRequest ticketRequest = new OrderRequest.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setAgeGroup(Ticket.AgeGroup.ADULT);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setEventId(1L);
        orderRequest.setCustomerName("John Doe");
        orderRequest.setEmail("john@example.com");
        orderRequest.setTickets(List.of(ticketRequest));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        assertThrows(SeatNotAvailableException.class, () -> {
            orderService.createOrder(orderRequest);
        });
    }

}

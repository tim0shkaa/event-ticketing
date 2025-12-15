package com.ticketing.service;

import com.ticketing.dto.OrderRequest;
import com.ticketing.dto.OrderResponse;
import com.ticketing.entity.*;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.exception.SeatNotAvailableException;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.OrderRepository;
import com.ticketing.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public OrderService(OrderRepository orderRepository, EventRepository eventRepository,
                       SeatRepository seatRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setEmail(request.getEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setEvent(event);
        order.setStatus(Order.OrderStatus.PENDING);

        List<Ticket> tickets = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderRequest.TicketRequest ticketRequest : request.getTickets()) {
            Seat seat = seatRepository.findById(ticketRequest.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + ticketRequest.getSeatId()));

            if (!seat.getAvailable()) {
                throw new SeatNotAvailableException("Seat " + seat.getSeatNumber() + " is not available");
            }

            if (!seat.getEvent().getId().equals(event.getId())) {
                throw new IllegalArgumentException("Seat does not belong to the specified event");
            }

            double basePrice = event.getBasePrice() * seat.getCategory().getPriceMultiplier();
            double ageGroupDiscount = ticketRequest.getAgeGroup().getDiscountMultiplier();
            double timeBasedDiscount = calculateTimeBasedDiscount(event.getEventDate());
            
            double totalDiscount = 1.0 - ((1.0 - ageGroupDiscount) + (1.0 - timeBasedDiscount));
            if (totalDiscount < 0) totalDiscount = 0;
            if (totalDiscount > 0.7) totalDiscount = 0.7;

            double finalPrice = basePrice * (1.0 - totalDiscount);

            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setOrder(order);
            ticket.setAgeGroup(ticketRequest.getAgeGroup());
            ticket.setPrice(finalPrice);
            ticket.setDiscountPercentage(totalDiscount * 100);

            tickets.add(ticket);
            totalPrice += finalPrice;

            seat.setAvailable(false);
            seatRepository.save(seat);
        }

        order.setTickets(tickets);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.CONFIRMED);

        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    private double calculateTimeBasedDiscount(LocalDateTime eventDate) {
        long daysUntilEvent = ChronoUnit.DAYS.between(LocalDateTime.now(), eventDate);

        if (daysUntilEvent > 30) {
            return 0.8;
        } else if (daysUntilEvent > 14) {
            return 0.9;
        } else if (daysUntilEvent > 7) {
            return 0.95;
        } else {
            return 1.0;
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToResponse(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        for (Ticket ticket : order.getTickets()) {
            Seat seat = ticket.getSeat();
            seat.setAvailable(true);
            seatRepository.save(seat);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setEmail(order.getEmail());
        response.setOrderDate(order.getOrderDate());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setEventId(order.getEvent().getId());
        response.setEventName(order.getEvent().getName());

        List<OrderResponse.TicketResponse> ticketResponses = order.getTickets().stream()
                .map(ticket -> {
                    OrderResponse.TicketResponse ticketResponse = new OrderResponse.TicketResponse();
                    ticketResponse.setId(ticket.getId());
                    ticketResponse.setSeatNumber(ticket.getSeat().getSeatNumber());
                    ticketResponse.setPrice(ticket.getPrice());
                    ticketResponse.setAgeGroup(ticket.getAgeGroup().name());
                    ticketResponse.setDiscountPercentage(ticket.getDiscountPercentage());
                    return ticketResponse;
                })
                .collect(Collectors.toList());

        response.setTickets(ticketResponses);
        return response;
    }

}

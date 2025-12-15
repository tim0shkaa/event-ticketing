package com.ticketing.service;

import com.ticketing.dto.EventResponse;
import com.ticketing.entity.Event;
import com.ticketing.entity.Seat;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> searchEventsByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private EventResponse convertToResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setEventDate(event.getEventDate());
        response.setVenue(event.getVenue());
        response.setBasePrice(event.getBasePrice());

        List<EventResponse.SeatResponse> seatResponses = event.getSeats().stream()
                .map(seat -> {
                    EventResponse.SeatResponse seatResponse = new EventResponse.SeatResponse();
                    seatResponse.setId(seat.getId());
                    seatResponse.setSeatNumber(seat.getSeatNumber());
                    seatResponse.setCategory(seat.getCategory().name());
                    seatResponse.setAvailable(seat.getAvailable());
                    seatResponse.setPrice(event.getBasePrice() * seat.getCategory().getPriceMultiplier());
                    return seatResponse;
                })
                .collect(Collectors.toList());

        response.setSeats(seatResponses);
        response.setTotalSeats(event.getSeats().size());
        response.setAvailableSeats((int) event.getSeats().stream()
                .filter(Seat::getAvailable)
                .count());

        return response;
    }

}

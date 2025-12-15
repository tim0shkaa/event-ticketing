package com.ticketing.controller;

import com.ticketing.dto.EventResponse;
import com.ticketing.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvents(@RequestParam String name) {
        return ResponseEntity.ok(eventService.searchEventsByName(name));
    }

}

package com.ticketing;

import com.ticketing.entity.Event;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.repository.EventRepository;
import com.ticketing.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void getEventById_Success() {
        Event event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now().plusDays(10));
        event.setVenue("Test Venue");
        event.setBasePrice(50.0);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        var result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals("Test Event", result.getName());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getEventById_NotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(1L);
        });

        verify(eventRepository, times(1)).findById(1L);
    }

}

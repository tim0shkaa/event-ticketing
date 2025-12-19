package com.ticketing;

import com.ticketing.entity.Event;
import com.ticketing.entity.Seat;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataInitializer(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {
        // Проверяем, не загружены ли уже данные
        if (eventRepository.count() > 0) {
            System.out.println("Data already initialized, skipping...");
            return;
        }
        
        System.out.println("Initializing data...");
        
        Event event1 = new Event();
        event1.setName("Rock Concert 2025");
        event1.setDescription("An amazing rock concert featuring top bands");
        event1.setEventDate(LocalDateTime.now().plusDays(30));
        event1.setVenue("Central Stadium");
        event1.setBasePrice(50.0);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setName("Classical Music Evening");
        event2.setDescription("A night of beautiful classical music");
        event2.setEventDate(LocalDateTime.now().plusDays(15));
        event2.setVenue("Opera House");
        event2.setBasePrice(80.0);
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setName("Comedy Show");
        event3.setDescription("Stand-up comedy with famous comedians");
        event3.setEventDate(LocalDateTime.now().plusDays(5));
        event3.setVenue("Comedy Club");
        event3.setBasePrice(30.0);
        eventRepository.save(event3);

        createSeatsForEvent(event1);
        createSeatsForEvent(event2);
        createSeatsForEvent(event3);
    }

    private void createSeatsForEvent(Event event) {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("VIP-" + i);
            seat.setCategory(Seat.SeatCategory.VIP);
            seat.setAvailable(true);
            seat.setEvent(event);
            seats.add(seat);
        }

        for (int i = 1; i <= 20; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("STD-" + i);
            seat.setCategory(Seat.SeatCategory.STANDARD);
            seat.setAvailable(true);
            seat.setEvent(event);
            seats.add(seat);
        }

        for (int i = 1; i <= 10; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("ECO-" + i);
            seat.setCategory(Seat.SeatCategory.ECONOMY);
            seat.setAvailable(true);
            seat.setEvent(event);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);
        
        System.out.println("Data initialization completed!");
    }

}

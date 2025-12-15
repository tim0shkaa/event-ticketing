package com.ticketing.repository;

import com.ticketing.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventIdAndAvailable(Long eventId, Boolean available);

    Optional<Seat> findByEventIdAndSeatNumber(Long eventId, String seatNumber);

    List<Seat> findByEventId(Long eventId);

}

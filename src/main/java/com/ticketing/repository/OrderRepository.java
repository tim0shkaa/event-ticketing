package com.ticketing.repository;

import com.ticketing.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByEmail(String email);

    List<Order> findByEventId(Long eventId);

}

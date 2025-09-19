package com.swiftlogistics.orchestrator.repository;

import com.swiftlogistics.orchestrator.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    // Find events by order ID
    List<Event> findByOrderIdOrderByTimestampDesc(String orderId);
    
    // Find failed events for monitoring
    @Query("{ 'status': 'FAILED' }")
    List<Event> findFailedEvents();
    
    // Find recent events for an order
    @Query("{ 'orderId': ?0, 'timestamp': { $gte: ?1 } }")
    List<Event> findRecentEventsByOrderId(String orderId, LocalDateTime since);
}
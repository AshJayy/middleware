package com.swiftlogistics.orchestrator.repository;

import com.swiftlogistics.orchestrator.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find orders by customer
    List<Order> findByCustomerId(String customerId);
    
    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);
    
    // Find orders by correlation ID (for tracking across systems)
    Optional<Order> findByCorrelationId(String correlationId);

}
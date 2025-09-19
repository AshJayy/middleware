package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.model.Event;
import com.swiftlogistics.orchestrator.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Log an event to MongoDB for auditing
     */
    public void logEvent(String orderId, String correlationId, String eventType,
                         String source, String description) {
        logEvent(orderId, correlationId, eventType, source, description, null, "SUCCESS", null);
    }

    /**
     * Log an event with additional payload
     */
    public Event logEvent(String orderId, String correlationId, String eventType, 
                         String source, String description, Map<String, Object> payload) {
        return logEvent(orderId, correlationId, eventType, source, description, payload, "SUCCESS", null);
    }

    /**
     * Log a failed event
     */
    public void logFailedEvent(String orderId, String correlationId, String eventType,
                               String source, String description, String errorMessage) {
        logEvent(orderId, correlationId, eventType, source, description, null, "FAILED", errorMessage);
    }

    /**
     * Comprehensive event logging method
     */
    public Event logEvent(String orderId, String correlationId, String eventType, 
                         String source, String description, Map<String, Object> payload, 
                         String status, String errorMessage) {
        try {
            Event event = Event.builder()
                    .orderId(orderId)
                    .correlationId(correlationId)
                    .eventType(Event.EventType.valueOf(eventType))
                    .source(Event.EventSource.valueOf(source))
                    .description(description)
                    .payload(payload != null ? payload : new HashMap<>())
                    .timestamp(LocalDateTime.now())
                    .status(Event.EventStatus.valueOf(status))
                    .errorMessage(errorMessage)
                    .build();

            Event savedEvent = eventRepository.save(event);
            
            log.info("Event logged: orderId={}, correlationId={}, eventType={}, source={}, status={}", 
                    orderId, correlationId, eventType, source, status);
            
            return savedEvent;
            
        } catch (Exception e) {
            log.error("Failed to log event: orderId={}, correlationId={}, eventType={}", 
                    orderId, correlationId, eventType, e);
            throw e;
        }
    }

    /**
     * Get all events for an order
     */
    public List<Event> getOrderEvents(String orderId) {
        return eventRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }

    /**
     * Get events by correlation ID (across all orders for the same flow)
     */
    public List<Event> getEventsByCorrelationId(String correlationId) {
        return eventRepository.findByCorrelationIdOrderByTimestampDesc(correlationId);
    }

    /**
     * Get failed events for monitoring
     */
    public List<Event> getFailedEvents() {
        return eventRepository.findFailedEvents();
    }

    /**
     * Get recent events for an order (last hour)
     */
    public List<Event> getRecentOrderEvents(String orderId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return eventRepository.findRecentEventsByOrderId(orderId, oneHourAgo);
    }

    /**
     * Create payload for common order events
     */
    public Map<String, Object> createOrderPayload(String orderId, String status, Object additionalData) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("status", status);
        payload.put("timestamp", LocalDateTime.now());
        
        if (additionalData != null) {
            payload.put("data", additionalData);
        }
        
        return payload;
    }
}
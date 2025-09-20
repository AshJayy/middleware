package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.model.Event;
import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventStatus;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import com.swiftlogistics.orchestrator.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Log an event to MongoDB for auditing
     */
    public void logSuccessEvent(String orderId, EventType eventType, EventSource source, String description) {
        logEvent(orderId, eventType, source, description, EventStatus.SUCCESS);
    }

    /**
     * Log a failed event
     */
    public void logFailedEvent(String orderId, EventType eventType, EventSource source, String description) {
        logEvent(orderId, eventType, source, description,  EventStatus.FAILED);
    }

    /**
     * Log a pending event
     */
    public void logPendingEvent(String orderId, EventType eventType, EventSource source, String description) {
        logEvent(orderId, eventType, source, description, EventStatus.PENDING);
    }

    /**
     * Comprehensive event logging method
     */
    public void logEvent(String orderId, EventType eventType, EventSource source, String description, EventStatus status) {
        try {
            Event event = Event.builder()
                    .orderId(orderId)
                    .eventType(eventType)
                    .source(source)
                    .description(description)
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .build();

            eventRepository.save(event);
            
            log.info("Event logged: orderId={}, eventType={}, source={}, status={}",
                    orderId, eventType, source, status);

        } catch (Exception e) {
            log.error("Failed to log event: orderId={}, eventType={}",
                    orderId, eventType, e);
            throw e;
        }
    }

    /**
     * Get all events for an order
     */
    public List<Event> getOrderEvents(String orderId) {
        return eventRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }

}
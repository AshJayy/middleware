package com.swiftlogistics.orchestrator.model;

import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventStatus;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {
    
    @Id
    private String eventId;
    
    @Indexed
    private String orderId;
    
    private EventType eventType;
    
    private EventSource source;
    
    private String description;
    
    // Success/failure status
    private EventStatus status;

    @CreatedDate
    private LocalDateTime timestamp;
}
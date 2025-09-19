package com.swiftlogistics.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAckMessage {
    private String orderId;
    private String status; // ACCEPTED or REJECTED
    private String message; // optional info, e.g., rejection reason
    private String address; // forward address info for ROS
}

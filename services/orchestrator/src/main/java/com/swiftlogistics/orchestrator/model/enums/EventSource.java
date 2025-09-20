package com.swiftlogistics.orchestrator.model.enums;

import lombok.Getter;

@Getter
public enum EventSource {
    ORCHESTRATOR,
    CMS_ADAPTER,
    WMS_ADAPTER,
    ROS_ADAPTER,
    DRIVER_APP,
    CLIENT_PORTAL
}

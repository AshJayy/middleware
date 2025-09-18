package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.messaging.publisher.WebSocketPublisher;
import com.swiftlogistics.orchestrator.model.Order;
import com.swiftlogistics.orchestrator.model.Route;
import com.swiftlogistics.orchestrator.repository.OrderRepository;
import com.swiftlogistics.orchestrator.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

  private final OrderRepository orderRepository;
  private final RouteRepository routeRepository;
  private final EventService eventService;
  private final WebSocketPublisher webSocketPublisher;


  public void createOrderRoute(String orderId, List<String> waypoints, String driverId) {
    try {
      Optional<Order> order = orderRepository.findById(orderId);
      if (order.isEmpty()) {
        throw new RuntimeException("Order not found: " + orderId);
      }

      Route route = Route.builder()
          .waypoints(waypoints)
          .driverId(driverId)
          .orderId(orderId)
          .createdAt(LocalDateTime.now())
          .build();

      Route newRoute = routeRepository.save(route);

      // Log event
      eventService.logEvent(orderId, route.getRouteId(), "ROUTE_COMPLETED",
          "ORCHESTRATOR", "Route planning completed and driver assigned");

      // Publish real-time update via WebSocket
      webSocketPublisher.publishOrderStatusUpdate(orderId, "ROUTED", order);

      log.info("Order route updated: orderId={}, driverId={}", orderId, driverId);

    } catch (Exception e) {
      log.error("Failed to update order route: orderId={}", orderId, e);
      throw e;
    }
  }

}

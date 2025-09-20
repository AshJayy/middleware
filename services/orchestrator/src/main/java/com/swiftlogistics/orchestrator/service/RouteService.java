package com.swiftlogistics.orchestrator.service;

import com.swiftlogistics.orchestrator.messaging.publisher.SsePublisher;
import com.swiftlogistics.orchestrator.model.Order;
import com.swiftlogistics.orchestrator.model.Route;
import com.swiftlogistics.orchestrator.model.enums.EventSource;
import com.swiftlogistics.orchestrator.model.enums.EventType;
import com.swiftlogistics.orchestrator.model.enums.OrderStatus;
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


  public Route createOrderRoute(String orderId, List<String> waypoints, String driverId) {
    try {
      Optional<Order> order = orderRepository.findById(orderId);
      if (order.isEmpty()) {
        throw new RuntimeException("Order not found: " + orderId);
      }

      Route route = Route.builder()
          .waypoints(waypoints)
          .createdAt(LocalDateTime.now())
          .build();

      Route newRoute = routeRepository.save(route);
        Order updatedOrder = order.get();
        updatedOrder.setRouteId(newRoute.getRouteId());
        updatedOrder.setDriverId(driverId);
        updatedOrder.setStatus(OrderStatus.ROUTED);
        updatedOrder.setRoutedAt(LocalDateTime.now());
        orderRepository.save(updatedOrder);

      // Log event
      eventService.logSuccessEvent(
            orderId,
            EventType.ROUTE_CREATED,
            EventSource.ROS_ADAPTER,
            "Route created with driverId: " + driverId
      );

      log.info("Order route updated: orderId={}, driverId={}", orderId, driverId);

        return newRoute;

    } catch (Exception e) {
      log.error("Failed to update order route: orderId={}", orderId, e);
      throw e;
    }
  }

}

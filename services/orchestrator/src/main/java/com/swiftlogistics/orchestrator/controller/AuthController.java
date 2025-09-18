package com.swiftlogistics.orchestrator.controller;

import com.swiftlogistics.orchestrator.model.Customer;
import com.swiftlogistics.orchestrator.model.Driver;
import com.swiftlogistics.orchestrator.dto.LoginRequest;
import com.swiftlogistics.orchestrator.repository.CustomerRepository;
import com.swiftlogistics.orchestrator.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
  private final CustomerRepository customerRepository;
  private final DriverRepository driverRepository;

//  Mock Auth functionality
  @PostMapping("/customer")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    Customer customer = customerRepository.findCustomerByCustomerName(loginRequest.getUsername());
    if (customer == null) {
      return ResponseEntity.status(401).body("Customer not found");
    }
    if (!customer.getPassword().equals(loginRequest.getPassword())) {
      return ResponseEntity.status(401).body("Invalid password");
    }
    // Remove password before returning customer details
    customer.setPassword(null);
    return ResponseEntity.ok(customer);
  }

  @PostMapping("/driver")
  public ResponseEntity<?> driverLogin(@RequestBody LoginRequest loginRequest) {
    Driver driver = driverRepository.findDriverByDriverName(loginRequest.getUsername());
    // Mock driver authentication logic
    if (driver == null) {
      return ResponseEntity.status(401).body("Driver not found");
    }
    if (!driver.getPassword().equals(loginRequest.getPassword())) {
      return ResponseEntity.ok("Invalid password");
    }

    driver.setPassword(null);
    return ResponseEntity.ok(driver);
  }
}

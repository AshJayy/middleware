package com.swiftlogistics.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver {
  @Id
  private String driverId;
  private String driverName;
  private String vehicleId;
  private boolean isAvailable;
  private String type;
  private String password;
}

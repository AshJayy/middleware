package com.swiftlogistics.orchestrator.repository;

import com.swiftlogistics.orchestrator.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverRepository extends MongoRepository<Driver, String> {

  Driver findDriverByDriverName(String driverName);

}

package com.swiftlogistics.orchestrator.repository;

import com.swiftlogistics.orchestrator.model.Route;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RouteRepository extends MongoRepository<Route, String> {

}

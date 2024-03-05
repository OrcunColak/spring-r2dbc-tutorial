package com.colak.springr2dbctutorial.repository;

import com.colak.springr2dbctutorial.jpa.Employee;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmployeeRepository extends R2dbcRepository<Employee, Integer> {

    Flux<Employee> findByFirstNameContains(String name);
}

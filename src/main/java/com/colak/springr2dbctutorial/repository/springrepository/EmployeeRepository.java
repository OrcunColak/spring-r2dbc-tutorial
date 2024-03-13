package com.colak.springr2dbctutorial.repository.springrepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import org.springframework.data.domain.Limit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring R2dbcRepository derives from Spring ReactiveCrudRepository
 */
@Repository
public interface EmployeeRepository extends R2dbcRepository<Employee, Integer> {

    Flux<Employee> findByFirstNameContains(String name);

    // Example for @Query
    @Query("select id, first_name, last_name from employee e where e.last_name = :lastname")
    Flux<Employee> findByLastName(String lastname);

    // Example for Limit
    Flux<Employee> findByLastName(String lastname, Limit limit);

}

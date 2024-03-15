package com.colak.springr2dbctutorial.repository.springrepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring R2dbcRepository derives from Spring ReactiveCrudRepository
 */
@Repository
public interface EmployeeRepository extends R2dbcRepository<Employee, Integer> {

    //******DERIVED QUERY******
    Flux<Employee> findByFirstNameContains(String name);

    // Example for Limit
    Flux<Employee> findByLastName(String lastname, Limit limit);

    // Example for pagination.
    // This query responds result set as a Flux as opposed to Page hence the total number of records needs to be queried
    // separately to populate Page response.
    Flux<Employee> findAllBy(Pageable pageable);

    //******CUSTOM QUERY******

    // Example for @Query
    @Query("select id, first_name, last_name from employee e where e.last_name = :lastname")
    Flux<Employee> findByLastName(String lastname);

}

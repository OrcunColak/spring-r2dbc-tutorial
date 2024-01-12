package com.colak.springr2dbctutorial.repository;

import com.colak.springr2dbctutorial.jpa.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeDatabaseClientRepository {

    private final DatabaseClient databaseClient;

    public Flux<Employee> findAll() {
        String sql = "SELECT id,first_name,last_name FROM employee";
        return databaseClient
                .sql(sql)
                .fetch()
                .all()
                .map(EmployeeDatabaseClientRepository::getEmployee);
    }

    public Mono<Employee> findById(Integer id) {
        String sql = "SELECT id,first_name,last_name FROM employee WHERE id = $1";
        return databaseClient.sql(sql)
                .bind(0, id)
                .fetch()
                .first()
                .map(EmployeeDatabaseClientRepository::getEmployee);
    }

    private static Employee getEmployee(Map<String, Object> row) {
        // DB column is BIGINT which corresponds to Long.
        // Convert Long to Integer
        Number number = (Number) row.get("id");
        Integer id = number.intValue();

        String firstName = (String) row.get("first_name");
        String lastName = (String) row.get("last_name");

        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        return employee;
    }


}

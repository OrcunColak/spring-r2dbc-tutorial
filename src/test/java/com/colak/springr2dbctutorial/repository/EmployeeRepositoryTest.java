package com.colak.springr2dbctutorial.repository;

import com.colak.springr2dbctutorial.jpa.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    void findAll() {
        Flux<Employee> employeeFlux = employeeRepository.findAll();

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<Employee> employeeMono = employeeRepository.findById(1);

        StepVerifier.create(employeeMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByFirstNameContains() {
        Flux<Employee> employeeFlux = employeeRepository.findByFirstNameContains("employee");

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteById() {
        Mono<Void> numberOfRows = employeeRepository.deleteById(1);

        StepVerifier.create(numberOfRows)
                .verifyComplete();
    }

    @Test
    void save() {
        Employee employee = new Employee();
        employee.setId(5);
        employee.setFirstName("employee5");
        employee.setLastName("lastname5");
        Mono<Employee> savedEmployee = employeeRepository.save(employee);

        StepVerifier.create(savedEmployee)
                .verifyComplete();
    }

    @Test
    void update() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("employee11");
        employee.setLastName("lastname11");
        Mono<Employee> updatedEmployee = employeeRepository.save(employee);

        StepVerifier.create(updatedEmployee)
                .expectNextCount(1)
                .verifyComplete();
    }

}

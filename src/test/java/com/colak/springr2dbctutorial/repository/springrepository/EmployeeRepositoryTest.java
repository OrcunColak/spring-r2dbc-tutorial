package com.colak.springr2dbctutorial.repository.springrepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    @Order(1)
    void findAll() {
        Flux<Employee> employeeFlux = employeeRepository.findAll();

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @Order(2)
    void findAllPaged() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("first_name").descending());
        Flux<Employee> employeeFlux = employeeRepository.findAllBy(pageRequest);

        List<Employee> expectedList = List.of(
                new Employee(4, "employee4", "lastname4", false),
                new Employee(3, "employee3", "lastname3", false)
        );

        StepVerifier.create(employeeFlux)
                .expectNextSequence(expectedList)
                .expectComplete()
                .verify();
    }

    @Test
    @Order(3)
    void findById() {
        Mono<Employee> employeeMono = employeeRepository.findById(1);

        StepVerifier.create(employeeMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(4)
    void findByFirstNameContains() {
        Flux<Employee> employeeFlux = employeeRepository.findByFirstNameContains("employee");

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @Order(5)
    void findByLastname() {
        Flux<Employee> employeeFlux = employeeRepository.findByLastName("lastname1");

        StepVerifier.create(employeeFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(6)
    void findByLastnameWithLimit() {
        Flux<Employee> employeeFlux = employeeRepository.findByLastName("lastname1", Limit.of(2));

        StepVerifier.create(employeeFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(7)
    void deleteById() {
        Mono<Void> numberOfRows = employeeRepository.deleteById(1);

        StepVerifier.create(numberOfRows)
                .verifyComplete();
    }

    @Test
    @Order(8)
    void save() {
        Employee employee = new Employee();
        employee.setId(5);
        employee.setFirstName("employee5");
        employee.setLastName("lastname5");
        employee.setNew(true);

        Mono<Employee> savedEmployee = employeeRepository.save(employee);

        StepVerifier.create(savedEmployee)
                .expectNextMatches(savedEntity -> savedEntity.getId() != null)
                .verifyComplete();
    }

    @Test
    @Order(9)
    void update() {
        Employee employee = new Employee();
        employee.setId(3);
        employee.setFirstName("employee33");
        employee.setLastName("lastname33");
        Mono<Employee> updatedEmployee = employeeRepository.save(employee);

        StepVerifier.create(updatedEmployee)
                .expectNextCount(1)
                .verifyComplete();
    }

}

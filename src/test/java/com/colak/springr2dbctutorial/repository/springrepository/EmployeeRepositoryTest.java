package com.colak.springr2dbctutorial.repository.springrepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Limit;
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
    void findByLastname() {
        Flux<Employee> employeeFlux = employeeRepository.findByLastName("lastname1");

        StepVerifier.create(employeeFlux)
                .expectNextCount(1)
                .verifyComplete();
    }
    @Test
    void findByLastnameWithLimit() {
        Flux<Employee> employeeFlux = employeeRepository.findByLastName("lastname1", Limit.of(2));

        StepVerifier.create(employeeFlux)
                .expectNextCount(1)
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
        employee.setNew(true);

        Mono<Employee> savedEmployee = employeeRepository.save(employee);

        StepVerifier.create(savedEmployee)
                .expectNextMatches(savedEntity -> savedEntity.getId() != null)
                .verifyComplete();
    }

    @Test
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

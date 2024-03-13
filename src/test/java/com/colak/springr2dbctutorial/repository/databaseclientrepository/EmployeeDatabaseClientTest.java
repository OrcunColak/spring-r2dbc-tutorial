package com.colak.springr2dbctutorial.repository.databaseclientrepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import com.colak.springr2dbctutorial.repository.databaseclientrepository.EmployeeDatabaseClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class EmployeeDatabaseClientTest {

    @Autowired
    private EmployeeDatabaseClientRepository employeeDatabaseClientRepository;


    @Test
    void findAll() {
        Flux<Employee> employeeFlux = employeeDatabaseClientRepository.findAll();

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<Employee> employeeMono = employeeDatabaseClientRepository.findById(1);

        StepVerifier.create(employeeMono)
                .expectNextCount(1)
                .verifyComplete();
    }

}

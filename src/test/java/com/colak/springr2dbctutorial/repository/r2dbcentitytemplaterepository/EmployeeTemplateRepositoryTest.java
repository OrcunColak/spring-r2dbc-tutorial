package com.colak.springr2dbctutorial.repository.r2dbcentitytemplaterepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import com.colak.springr2dbctutorial.repository.r2dbcentitytemplaterepository.EmployeeTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class EmployeeTemplateRepositoryTest {

    @Autowired
    private EmployeeTemplateRepository employeeTemplateRepository;


    @Test
    void findAll() {
        Flux<Employee> employeeFlux = employeeTemplateRepository.findAll();

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<Employee> employeeMono = employeeTemplateRepository.findById(1);

        StepVerifier.create(employeeMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByFirstNameContains() {
        Flux<Employee> employeeFlux = employeeTemplateRepository.findByFirstNameContains("employee");

        StepVerifier.create(employeeFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteById() {
        Mono<Long> numberOfRows = employeeTemplateRepository.deleteById(1);

        StepVerifier.create(numberOfRows)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save() {
        Employee employee = new Employee();
        employee.setId(5);
        employee.setFirstName("employee5");
        employee.setLastName("lastname5");
        employee.setNew(true);
        Mono<Integer> id = employeeTemplateRepository.save(employee);

        StepVerifier.create(id)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    void update() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("employee11");
        employee.setLastName("lastname11");
        Mono<Long> numberOfRows = employeeTemplateRepository.update(employee);

        StepVerifier.create(numberOfRows)
                .expectNextCount(1)
                .verifyComplete();
    }
}

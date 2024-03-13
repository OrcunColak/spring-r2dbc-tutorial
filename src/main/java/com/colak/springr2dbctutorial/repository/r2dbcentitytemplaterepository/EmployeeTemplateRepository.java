package com.colak.springr2dbctutorial.repository.r2dbcentitytemplaterepository;

import com.colak.springr2dbctutorial.jpa.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeTemplateRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Flux<Employee> findAll() {
        return r2dbcEntityTemplate
                .select(Employee.class)
                .all();
    }

    public Mono<Employee> findById(Integer id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return this.r2dbcEntityTemplate
                .selectOne(query, Employee.class);
    }

    public Flux<Employee> findByFirstNameContains(String name) {
        Query query = Query.query(Criteria.where("first_name")
                        .like("%" + name + "%"))
                .limit(10)
                .offset(0);
        return this.r2dbcEntityTemplate.select(Employee.class)
                .matching(query)
                .all();
    }

    public Mono<Long> deleteById(Integer id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return this.r2dbcEntityTemplate
                .delete(query, Employee.class);
    }

    public Mono<Long> update(Employee employee) {
/*
        return this.template.update(Post.class)
                .matching(Query.query(where("id").is(p.getId())))
                .apply(Update.update("title", p.getTitle())
                        .set("content", p.getContent())
                        .set("status", p.getStatus())
                        .set("metadata", p.getMetadata()));
*/
        Query query = Query.query(Criteria.where("id").is(employee.getId()));
        Update update = Update.update("first_name", employee.getFirstName())
                .set("last_name", employee.getLastName());
        return this.r2dbcEntityTemplate.update(
                query,
                update,
                Employee.class);

    }

    public Mono<Integer> save(Employee employee) {
        return this.r2dbcEntityTemplate.insert(Employee.class)
                .using(employee)
                .map(post -> employee.getId());
    }
}

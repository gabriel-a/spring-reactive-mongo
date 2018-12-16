package com.openprogrammer.reactive.springreactivemongo.resource;

import com.openprogrammer.reactive.springreactivemongo.AbstractSpringIntegrationTest;
import com.openprogrammer.reactive.springreactivemongo.model.Employee;
import com.openprogrammer.reactive.springreactivemongo.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;


public class EmployeeResourceTest extends AbstractSpringIntegrationTest{

    @Autowired
    EmployeeRepository employeeRepository;

    final static String API_EMPLOYEE = "/api/employee/";

    @Test
    public void get() {
        employeeRepository.save(new Employee(UUID.randomUUID().toString(), "Test0", 1100L)).block();

        this.webTestClient.get().uri(API_EMPLOYEE)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Employee.class);
    }

    @Test
    public void addEmployee() {

        Employee employee = new Employee(UUID.randomUUID().toString(), "Test1", 1500L);

        this.webTestClient.post().uri(API_EMPLOYEE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Test1")
                .jsonPath("$.salary").isEqualTo(1500L);
    }

    @Test
    public void updateEmployee() {
        Employee employee = employeeRepository.save(new Employee(UUID.randomUUID().toString(), "Test2", 1300L)).block();

        this.webTestClient.put()
                .uri(API_EMPLOYEE +"{id}", Collections.singletonMap("id", employee.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Test2")
                .jsonPath("$.salary").isEqualTo(1300L);
    }

    @Test
    public void findEmployeeById() {
        Employee employee = employeeRepository.save(new Employee(UUID.randomUUID().toString(), "Test3", 1300L)).block();

        this.webTestClient.get()
                .uri(API_EMPLOYEE +"{id}", Collections.singletonMap("id", employee.getId()))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(response ->
                        Assertions.assertThat(response.getResponseBody()).isNotNull());
    }

    @Test
    public void deleteEmloyeeById() {
        Employee employee = employeeRepository.save(new Employee(UUID.randomUUID().toString(), "Test4", 1300L)).block();

        this.webTestClient.delete()
                .uri(API_EMPLOYEE +"{id}", Collections.singletonMap("id", employee.getId()))
                .exchange()
                .expectStatus().isOk();
    }
}
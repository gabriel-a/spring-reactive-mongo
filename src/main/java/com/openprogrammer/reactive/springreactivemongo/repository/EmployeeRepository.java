package com.openprogrammer.reactive.springreactivemongo.repository;


import com.openprogrammer.reactive.springreactivemongo.model.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
}

package com.openprogrammer.reactive.springreactivemongo;

import com.openprogrammer.reactive.springreactivemongo.model.Employee;
import com.openprogrammer.reactive.springreactivemongo.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class SpringReactiveMongoApplication {

	//This should be moved to a database.
	@Bean
	CommandLineRunner employees(EmployeeRepository employeeRepository){
		return args -> employeeRepository
				.deleteAll()
				.subscribe(null, null, () -> {
					Stream.of(new Employee(UUID.randomUUID().toString(), "Jean", 1500L),
							new Employee(UUID.randomUUID().toString(), "Dan", 2500L),
							new Employee(UUID.randomUUID().toString(), "Tom", 3500L),
							new Employee(UUID.randomUUID().toString(), "Martin", 4500L))
					.forEach(employee -> employeeRepository.save(employee)
					.subscribe(emp -> System.out.println(emp)));
				});
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveMongoApplication.class, args);
	}

}


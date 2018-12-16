package com.openprogrammer.reactive.springreactivemongo.resource;

import com.openprogrammer.reactive.springreactivemongo.model.Employee;
import com.openprogrammer.reactive.springreactivemongo.model.EmployeeEvent;
import com.openprogrammer.reactive.springreactivemongo.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/employee")
public class EmployeeResource {

    public EmployeeResource(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    private EmployeeRepository employeeRepository;

    @GetMapping("/")
    public Flux<Employee> Get(){
        return employeeRepository.findAll();
    }

    @PostMapping("/")
    Mono<Employee> addEmployee(@RequestBody Employee employee) {
        employee.setId(UUID.randomUUID().toString());
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Employee>> updateEmployee(@PathVariable(value = "id") String employeeId, @RequestBody Employee employee) {
        return employeeRepository.findById(employeeId)
                .flatMap(emp -> {
                    emp.setName(employee.getName());
                    emp.setSalary(employee.getSalary());
                    return employeeRepository.save(emp);
                })
                .map(updatedEmployee -> new ResponseEntity<>(updatedEmployee, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Employee>> findEmployeeById(@PathVariable("id") final String id) {
        return employeeRepository.findById(id)
            .map(employee -> ResponseEntity.ok(employee))
            .defaultIfEmpty(ResponseEntity.notFound().build()); }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEmployee(@PathVariable(value = "id") String id) {

        return employeeRepository.findById(id)
                .flatMap(existingTweet ->
                        employeeRepository.delete(existingTweet)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EmployeeEvent> findEventByEmployeeId(@PathVariable("id") final String id) {
        return  employeeRepository
                .findById(id)
                .flatMapMany(employee -> {
                    Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));

                    Flux<EmployeeEvent> employeeEventFlux =
                            Flux.fromStream(
                                    Stream.generate(() -> new EmployeeEvent(employee, new Date()))
                            );

                    return Flux.zip(interval, employeeEventFlux).map(Tuple2::getT2);
                });
    }
}

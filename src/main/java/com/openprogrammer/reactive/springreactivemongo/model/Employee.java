package com.openprogrammer.reactive.springreactivemongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
@Document
public class Employee {

    @Id
    String id;
    @NotBlank
    @Size(max = 140)
    String name;
    @NotBlank
    Long salary;
}

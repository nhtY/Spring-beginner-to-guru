package com.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// instead of @Data use @Getter and @Setter
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity // make it a jpa entity
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID") // I will use a generator called UUID
    @GenericGenerator(name = "UUID", type = org.hibernate.id.uuid.UuidGenerator.class) // Here is that UUID
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;
    private String name;

    @Version // at the beginning it is 0. After every change, it is incremented by 1
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}

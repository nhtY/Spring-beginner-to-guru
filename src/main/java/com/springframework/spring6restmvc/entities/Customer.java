package com.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class) // Here is that UUID
    @JdbcTypeCode(SqlTypes.CHAR) // fix: hibernate perceives it as binary. Tell this is a char
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;
    private String name;

    @Version // at the beginning it is 0. After every change, it is incremented by 1
    private Integer version;

    @Column(length = 255)
    private String email;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder.Default // builder creates a null set but here we initialized it. So, we say 'use my code as default'
    @OneToMany(mappedBy = "customer") // customer here is a property in the target entity. (BeerOrder must have a property called customer)
    private Set<BeerOrder> beerOrders = new HashSet<>();
}

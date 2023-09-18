package com.springframework.spring6restmvc.entities;

import com.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// instead of @Data use @Getter and @Setter
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity // make it a jpa entity
public class Beer {
    @Id
    @GeneratedValue(generator = "UUID") // I will use a generator called UUID
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class) // Here is that UUID
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;
    @Version
    private Integer version;

    @NotNull
    @NotBlank
    private String beerName;
    @NotNull
    private BeerStyle beerStyle;
    @NotNull
    @NotBlank
    private String upc;
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}

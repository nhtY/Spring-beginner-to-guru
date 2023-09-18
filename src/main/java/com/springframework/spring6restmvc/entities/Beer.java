package com.springframework.spring6restmvc.entities;

import com.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.CHAR) // fix: hibernate perceives it as binary. Tell this is a char
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;
    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Size(max = 50) // checked BEFORE writing to DB
    @Column(length = 50) // checked while writing to DB
    private String beerName;
    @NotNull
    private BeerStyle beerStyle;
    @NotNull
    @NotBlank
    @Column(length = 10) // checked while writing to DB
    private String upc;
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}

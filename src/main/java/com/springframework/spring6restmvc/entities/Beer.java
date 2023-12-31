package com.springframework.spring6restmvc.entities;

import com.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "beer")
    private Set<BeerOrderLine> beerOrderLines;

    @Builder.Default // use my code as default when using builder method.
    @ManyToMany
    // A table between Beer and Category to establish a many-to-many relation (here the name must be the same given in Category class --> otherwise SchemaManagementException: missing table)
    @JoinTable(
            name = "beer_category",
            joinColumns = @JoinColumn(name = "beer_id"), // FK, coming from Beer (beer_id is the id of Beer--> this class: owner)
            inverseJoinColumns = @JoinColumn(name = "category_id") // FK, coming from Category (category_id is id of Category)
    )
    private Set<Category> categories = new HashSet<>();

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBeers().remove(this);
    }

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}

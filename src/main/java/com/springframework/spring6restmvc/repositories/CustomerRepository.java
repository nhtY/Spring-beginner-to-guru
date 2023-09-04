package com.springframework.spring6restmvc.repositories;

import com.springframework.spring6restmvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// CrudRepository could be extended, but JpaRepository already extends it. Besides Jpa repository provides jpa-specific methods: flushing JPA session etc.
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}

package com.springframework.spring6restmvc.mappers;

import com.springframework.spring6restmvc.entities.Customer;
import com.springframework.spring6restmvc.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper//(componentModel = "spring")
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDto(Customer customer);
}

package com.clothifystore.repository;

import com.clothifystore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Integer> {

    Optional<Customer> findByUserEmail(String email);
    Boolean existsByUser_Email(String email);
    List<Customer> findAllByCustomerIDIn(Collection<Integer> customerIdCollection);

}

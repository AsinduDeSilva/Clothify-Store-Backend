package com.clothifystore.repository;

import com.clothifystore.entity.Order;
import com.clothifystore.enums.OrderStatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer> {

    Page<Order> findByCustomerID(int customerID, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAllByStatus(OrderStatusTypes status, Pageable pageable);
    Integer countByDate(LocalDate localDate);
    List<Order> findByDateAfter(LocalDate localDate);
    List<Order> findByDate(LocalDate localDate);
    Integer countByStatus(OrderStatusTypes orderStatusTypes);
    Integer countByCustomerIDAndStatusNotIn(int customerID, List<OrderStatusTypes> orderStatusTypes);

}

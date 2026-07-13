package com.clothifystore.service;

import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.dto.response.WeekOrderDataResponseDTO;
import com.clothifystore.entity.Order;
import org.springframework.data.domain.Page;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface OrderService {
    void addOrder(Order order) throws MessagingException, UnsupportedEncodingException;
    Order getOrder(int orderID);
    Page<Order> getOrdersByStatus(int status, int page);
    Page<Order> getOrdersOfCustomer(int customerID, int page);
    int getOngoingOrderCountOfCustomer(int customerID);
    void updateOrderStatus(int orderID, int status) throws MessagingException, UnsupportedEncodingException;
    WeekOrderDataResponseDTO getTotalOrdersCountOfPast7Days();
    OrderStatsResponseDTO getSalesIncome();
}

package com.clothifystore.service;

import com.clothifystore.dto.request.OrderRequestDTO;
import com.clothifystore.dto.response.OrderResponseDTO;
import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.dto.response.WeekOrderDataResponseDTO;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface OrderService {
    void addOrder(OrderRequestDTO request) throws MessagingException, UnsupportedEncodingException;
    OrderResponseDTO getOrder(int orderID);
    Page<OrderResponseDTO> getOrdersByStatus(int status, int page);
    Page<OrderResponseDTO> getOrdersOfCustomer(int customerID, int page);
    int getOngoingOrderCountOfCustomer(int customerID);
    void updateOrderStatus(int orderID, int status) throws MessagingException, UnsupportedEncodingException;
    WeekOrderDataResponseDTO getTotalOrdersCountOfPast7Days();
    OrderStatsResponseDTO getSalesIncome();
}

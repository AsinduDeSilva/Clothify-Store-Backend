package com.clothifystore.service.impl;

import com.clothifystore.service.EmailService;
import com.clothifystore.service.OrderService;

import com.clothifystore.dto.response.OrderStatsResponseDTO;
import com.clothifystore.dto.response.WeekOrderDataResponseDTO;
import com.clothifystore.entity.Order;
import com.clothifystore.entity.OrderDetail;
import com.clothifystore.entity.Product;
import com.clothifystore.enums.OrderStatusTypes;
import com.clothifystore.exception.InsufficientStockException;
import com.clothifystore.exception.InvalidRequestException;
import com.clothifystore.exception.ResourceNotFoundException;
import com.clothifystore.repository.CustomerRepo;
import com.clothifystore.repository.OrderRepo;
import com.clothifystore.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private EmailService emailService;

    public void addOrder(Order order) throws MessagingException, UnsupportedEncodingException {

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = productRepo.findById(orderDetail.getProductID())
                    .orElseThrow(() -> new ResourceNotFoundException("Some products not found"));

            int restQty;
            switch (orderDetail.getSize()) {
                case SMALL:
                    restQty = product.getSmallQty() - orderDetail.getQuantity();
                    break;
                case MEDIUM:
                    restQty = product.getMediumQty() - orderDetail.getQuantity();
                    break;
                case LARGE:
                    restQty = product.getLargeQty() - orderDetail.getQuantity();
                    break;
                default:
                    throw new InvalidRequestException("Invalid size");
            }

            if (restQty < 0) {
                throw new InsufficientStockException("Do not have enough stock");
            }

            switch (orderDetail.getSize()) {
                case SMALL:
                    product.setSmallQty(restQty);
                    break;
                case MEDIUM:
                    product.setMediumQty(restQty);
                    break;
                case LARGE:
                    product.setLargeQty(restQty);
            }
        }

        order.setDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        order.setStatus(OrderStatusTypes.PENDING);
        orderRepo.save(order);

        customerRepo.findById(order.getCustomerID()).ifPresent(customer -> {
            String emailBody = "<h1>Hey there, " + customer.getFirstName() + "</h1>"
                    + "Your order Received. Thank you for shopping with us.";
            try {
                emailService.sendEmail(customer.getUser().getEmail(), "Order Placed", emailBody);
            } catch (MessagingException | UnsupportedEncodingException e) {
                // Log exception if necessary
            }
        });
    }

    public Order getOrder(int orderID) {
        return orderRepo.findById(orderID)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Page<Order> getOrdersByStatus(int status, int page) {
        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 16, sort);
        switch (status) {
            case 0:
                return orderRepo.findAll(pageable);
            case 1:
                return orderRepo.findAllByStatus(OrderStatusTypes.PENDING, pageable);
            case 2:
                return orderRepo.findAllByStatus(OrderStatusTypes.PROCESSING, pageable);
            case 3:
                return orderRepo.findAllByStatus(OrderStatusTypes.OUT_FOR_DELIVERY, pageable);
            case 4:
                return orderRepo.findAllByStatus(OrderStatusTypes.DELIVERED, pageable);
            case 5:
                return orderRepo.findAllByStatus(OrderStatusTypes.CANCELLED, pageable);
            default:
                throw new InvalidRequestException("Invalid status");
        }
    }

    public Page<Order> getOrdersOfCustomer(int customerID, int page) {
        Sort sort = Sort.by(Sort.Order.desc("orderID"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);
        return orderRepo.findByCustomerID(customerID, pageable);
    }

    public int getOngoingOrderCountOfCustomer(int customerID) {
        List<OrderStatusTypes> orderStatusTypes = new ArrayList<>();
        orderStatusTypes.add(OrderStatusTypes.DELIVERED);
        orderStatusTypes.add(OrderStatusTypes.CANCELLED);
        return orderRepo.countByCustomerIDAndStatusNotIn(customerID, orderStatusTypes);
    }

    public void updateOrderStatus(int orderID, int status) throws MessagingException, UnsupportedEncodingException {
        Order order = orderRepo.findById(orderID)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        switch (status) {
            case 0:
                order.setStatus(OrderStatusTypes.PENDING);
                break;
            case 1:
                order.setStatus(OrderStatusTypes.PROCESSING);
                break;
            case 2:
                order.setStatus(OrderStatusTypes.OUT_FOR_DELIVERY);
                break;
            case 3:
                order.setStatus(OrderStatusTypes.DELIVERED);
                break;
            case 4:
                order.setStatus(OrderStatusTypes.CANCELLED);
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    productRepo.findById(orderDetail.getProductID()).ifPresent(product -> {
                        switch (orderDetail.getSize()) {
                            case SMALL:
                                product.setSmallQty(product.getSmallQty() + orderDetail.getQuantity());
                                break;
                            case MEDIUM:
                                product.setMediumQty(product.getMediumQty() + orderDetail.getQuantity());
                                break;
                            case LARGE:
                                product.setLargeQty(product.getLargeQty() + orderDetail.getQuantity());
                                break;
                        }
                    });
                }
                break;
            default:
                throw new InvalidRequestException("Invalid status");
        }
        orderRepo.save(order);

        customerRepo.findById(order.getCustomerID()).ifPresent(customer -> {
            String body = "<h1>Hey there, " + customer.getFirstName() + "</h1>"
                    + "Your order is " + order.getStatus().toString().toLowerCase().replace('_', ' ');

            try {
                emailService.sendEmail(customer.getUser().getEmail(), "Order Status Updated", body);
            } catch (MessagingException | UnsupportedEncodingException e) {
                // Log exception if necessary
            }
        });
    }

    public WeekOrderDataResponseDTO getTotalOrdersCountOfPast7Days() {
        List<Integer> orderCountList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (int i = 7; i > 0; i--) {
            LocalDate localDate = LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(i);
            dateList.add(localDate.toString().substring(5, 10));
            orderCountList.add(orderRepo.countByDate(localDate));
        }
        return new WeekOrderDataResponseDTO(orderCountList, dateList);
    }

    public OrderStatsResponseDTO getSalesIncome() {
        List<Order> ordersOfToday = orderRepo.findByDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        List<Order> ordersOfYesterday = orderRepo.findByDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1));
        List<Order> ordersOfLast30Days = orderRepo.findByDateAfter(LocalDate.now(ZoneId.of("Asia/Kolkata")).minusMonths(1));

        double incomeOfToday = 0;
        double incomeOfYesterday = 0;
        double incomeOfLast30Days = 0;

        for (Order order : ordersOfToday) {
            if (order.getStatus() == OrderStatusTypes.CANCELLED) {
                continue;
            }
            incomeOfToday += order.getTotal();
        }

        for (Order order : ordersOfYesterday) {
            if (order.getStatus() == OrderStatusTypes.CANCELLED) {
                continue;
            }
            incomeOfYesterday += order.getTotal();
        }

        for (Order order : ordersOfLast30Days) {
            if (order.getStatus() == OrderStatusTypes.CANCELLED) {
                continue;
            }
            incomeOfLast30Days += order.getTotal();
        }

        return new OrderStatsResponseDTO(
                incomeOfToday,
                incomeOfYesterday,
                incomeOfLast30Days,
                orderRepo.countByStatus(OrderStatusTypes.PENDING),
                orderRepo.countByStatus(OrderStatusTypes.PROCESSING),
                orderRepo.countByStatus(OrderStatusTypes.OUT_FOR_DELIVERY)
        );
    }
}

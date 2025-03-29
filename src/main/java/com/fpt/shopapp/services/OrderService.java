package com.fpt.shopapp.services;

import com.fpt.shopapp.dto.OrderDTO;
import com.fpt.shopapp.model.Order;
import com.fpt.shopapp.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderDTO orderDTO);
    OrderResponse getOrder(Long id);
    OrderResponse updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}

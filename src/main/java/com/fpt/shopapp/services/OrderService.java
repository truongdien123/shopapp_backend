package com.fpt.shopapp.services;

import com.fpt.shopapp.dto.OrderDTO;
import com.fpt.shopapp.responses.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderDTO orderDTO);
    OrderResponse getOrder(Long id);
    OrderResponse updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    List<OrderResponse> getAllOrders(Long userId);
}

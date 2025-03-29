package com.fpt.shopapp.services;

import com.fpt.shopapp.dto.OrderDetailDTO;
import com.fpt.shopapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO);
    OrderDetail getOrderDetail(Long id);
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO);
    void deleteOrderDetail(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}

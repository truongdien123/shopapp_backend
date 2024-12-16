package com.fpt.shopapp.services.imp;

import com.fpt.shopapp.dto.OrderDetailDTO;
import com.fpt.shopapp.exceptions.DataNotFoundException;
import com.fpt.shopapp.model.Order;
import com.fpt.shopapp.model.OrderDetail;
import com.fpt.shopapp.model.Product;
import com.fpt.shopapp.repositories.OrderDetailRepository;
import com.fpt.shopapp.repositories.OrderRepository;
import com.fpt.shopapp.repositories.ProductRepository;
import com.fpt.shopapp.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImp implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(()->new DataNotFoundException("Not found order"));
        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(()->new DataNotFoundException("Not found product"));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .price(orderDetailDTO.getPrice())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) {
        return orderDetailRepository.findById(id).orElseThrow(()->new DataNotFoundException("Not found this order detail"));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow(()->new DataNotFoundException("Not found this order detail"));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(()->new DataNotFoundException("Not found this order"));
        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(()->new DataNotFoundException("Not found this product"));
        orderDetail.setPrice(orderDetail.getPrice());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setTotalMoney(orderDetail.getTotalMoney());
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> getAllOrderDetail(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}

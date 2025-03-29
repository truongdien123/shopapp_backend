package com.fpt.shopapp.services.imp;

import com.fpt.shopapp.dto.CartItemDTO;
import com.fpt.shopapp.dto.OrderDTO;
import com.fpt.shopapp.exceptions.DataNotFoundException;
import com.fpt.shopapp.model.*;
import com.fpt.shopapp.repositories.OrderDetailRepository;
import com.fpt.shopapp.repositories.OrderRepository;
import com.fpt.shopapp.repositories.ProductRepository;
import com.fpt.shopapp.repositories.UserRepository;
import com.fpt.shopapp.responses.OrderResponse;
import com.fpt.shopapp.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDTO orderDTO) {
        // tìm xem user id có tồn tại hay không
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        // kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())){
            throw  new DataNotFoundException("Date must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(order.getTotalMoney());
        orderRepository.save(order);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Product not found"));
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Not found order"));
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Not found order"));
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Not found user"));
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public void deleteOrder(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }

}

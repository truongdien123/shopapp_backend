package com.fpt.shopapp.repositories;

import com.fpt.shopapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //Tìm các đơn hàng của 1 user nào đó
    List<Order> findByUserId(Long userId);

    @Query("select o from Order o where o.active = true and (:keyword is null or :keyword = '' or " +
            "o.fullName like %:keyword% or " +
            "o.address like %:keyword% or " +
            "o.note like %:keyword% or " +
            "o.email like %:keyword%)")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

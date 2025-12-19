package com.homeqadam.backend.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Все заказы конкретного пользователя, новые сверху
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}

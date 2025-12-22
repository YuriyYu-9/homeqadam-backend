package com.homeqadam.backend.order;

import com.homeqadam.backend.category.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Доступные для мастера заказы по категории (NEW)
    List<Order> findByStatusAndCategoryOrderByCreatedAtDesc(OrderStatus status, ServiceCategory category);

    // Принятые мастером заказы
    List<Order> findByTechnicianIdOrderByCreatedAtDesc(Long technicianId);
}

package com.homeqadam.backend.order;

import com.homeqadam.backend.category.ServiceCategory;
import com.homeqadam.backend.order.dto.OrderCustomerDto;
import com.homeqadam.backend.order.dto.OrderRequest;
import com.homeqadam.backend.order.dto.OrderResponse;
import com.homeqadam.backend.order.dto.OrderTechnicianDto;
import com.homeqadam.backend.profile.Profile;
import com.homeqadam.backend.profile.ProfileRepository;
import com.homeqadam.backend.user.Role;
import com.homeqadam.backend.user.User;
import com.homeqadam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // =========================
    // CREATE (CLIENT)
    // =========================
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (customer.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Only client can create order");
        }

        ServiceCategory category = parseCategory(request.getCategory());

        Order order = Order.builder()
                .customer(customer)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .scheduledAt(request.getScheduledAt())
                .status(OrderStatus.NEW)
                .build();

        Order saved = orderRepository.save(order);

        // Клиент создаёт — показываем контекст для клиента (technician будет null)
        return toClientResponse(saved);
    }

    // =========================
    // CLIENT
    // =========================
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toClientResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelByClient(Long orderId, Long clientId) {
        Order order = getOrderOrThrow(orderId);

        if (!order.getCustomer().getId().equals(clientId)) {
            throw new IllegalArgumentException("Not your order");
        }

        if (order.getStatus() != OrderStatus.NEW &&
                order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalArgumentException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelledBy(CancelledBy.CLIENT);

        Order saved = orderRepository.save(order);

        return toClientResponse(saved);
    }

    @Transactional
    public OrderResponse completeByClient(Long orderId, Long clientId) {
        Order order = getOrderOrThrow(orderId);

        if (!order.getCustomer().getId().equals(clientId)) {
            throw new IllegalArgumentException("Not your order");
        }

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Order is not in progress");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        return toClientResponse(saved);
    }

    // =========================
    // TECHNICIAN
    // =========================

    /**
     * Основной метод:
     * категория берётся из профиля мастера в БД.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAvailableOrdersForTechnician(Long technicianId) {
        User technician = getUserOrThrow(technicianId, Role.TECHNICIAN);

        Profile profile = profileRepository.findByUserIdAndUserRole(technician.getId(), Role.TECHNICIAN)
                .orElseThrow(() -> new IllegalStateException("Technician profile not found"));

        if (profile.getSpecialty() == null) {
            throw new IllegalStateException("Technician specialty not set");
        }

        ServiceCategory category = profile.getSpecialty();

        return orderRepository
                .findByStatusAndCategoryOrderByCreatedAtDesc(OrderStatus.NEW, category)
                .stream()
                .map(this::toTechnicianResponse) // мастер видит данные клиента
                .toList();
    }

    /**
     * Legacy-метод для ручных тестов.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAvailableOrdersForTechnicianLegacy(String categoryRaw) {
        ServiceCategory category = parseCategory(categoryRaw);

        return orderRepository
                .findByStatusAndCategoryOrderByCreatedAtDesc(OrderStatus.NEW, category)
                .stream()
                .map(this::toTechnicianResponse) // мастер видит данные клиента
                .toList();
    }

    @Transactional
    public OrderResponse acceptOrder(Long orderId, Long technicianId) {
        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalArgumentException("Order is not available");
        }

        User technician = getUserOrThrow(technicianId, Role.TECHNICIAN);

        order.setTechnician(technician);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Мастер принимает — показываем контекст для мастера (customer заполнен)
        return toTechnicianResponse(saved);
    }

    @Transactional
    public OrderResponse startOrder(Long orderId, Long technicianId) {
        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalArgumentException("Order is not accepted");
        }

        if (order.getTechnician() == null || !order.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException("Not your order");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setStartedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        return toTechnicianResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getTakenOrders(Long technicianId) {
        return orderRepository.findByTechnicianIdOrderByCreatedAtDesc(technicianId)
                .stream()
                .map(this::toTechnicianResponse) // мастер видит данные клиента
                .toList();
    }

    // =========================
    // MAPPERS (контекстные)
    // =========================

    /**
     * Ответ для CLIENT:
     * - technician заполняем (если назначен)
     * - customer не отдаём
     */
    private OrderResponse toClientResponse(Order order) {
        return baseResponse(order)
                .customer(null)
                .technician(buildTechnicianDto(order.getTechnician()))
                .build();
    }

    /**
     * Ответ для TECHNICIAN:
     * - customer заполняем
     * - technician не отдаём (т.к. это он сам)
     */
    private OrderResponse toTechnicianResponse(Order order) {
        return baseResponse(order)
                .customer(buildCustomerDto(order.getCustomer()))
                .technician(null)
                .build();
    }

    /**
     * Базовые поля ответа (без сторон).
     */
    private OrderResponse.OrderResponseBuilder baseResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .category(order.getCategory())
                .title(order.getTitle())
                .description(order.getDescription())
                .address(order.getAddress())
                .scheduledAt(order.getScheduledAt())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .acceptedAt(order.getAcceptedAt())
                .startedAt(order.getStartedAt())
                .completedAt(order.getCompletedAt())
                .cancelledAt(order.getCancelledAt())
                .cancelledBy(order.getCancelledBy());
    }

    private OrderCustomerDto buildCustomerDto(User customer) {
        if (customer == null) return null;

        Profile p = profileRepository.findByUserId(customer.getId()).orElse(null);

        return OrderCustomerDto.builder()
                .userId(customer.getId())
                .firstName(p != null ? p.getFirstName() : null)
                .lastName(p != null ? p.getLastName() : null)
                .phone(p != null ? p.getPhone() : null)
                .telegram(p != null ? p.getTelegram() : null)
                .build();
    }

    private OrderTechnicianDto buildTechnicianDto(User technician) {
        if (technician == null) return null;

        Profile p = profileRepository.findByUserIdAndUserRole(technician.getId(), Role.TECHNICIAN).orElse(null);

        return OrderTechnicianDto.builder()
                .userId(technician.getId())
                .firstName(p != null ? p.getFirstName() : null)
                .lastName(p != null ? p.getLastName() : null)
                .specialty(p != null ? p.getSpecialty() : null)
                .experienceYears(p != null ? p.getExperienceYears() : null)
                .avatarUrl(p != null ? p.getAvatarUrl() : null)
                .telegram(p != null ? p.getTelegram() : null)
                .build();
    }

    // =========================
    // HELPERS
    // =========================
    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private User getUserOrThrow(Long userId, Role requiredRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != requiredRole) {
            throw new IllegalArgumentException("Invalid role");
        }
        return user;
    }

    private ServiceCategory parseCategory(String raw) {
        try {
            return ServiceCategory.valueOf(raw.trim().toUpperCase());
        } catch (Exception ex) {
            String allowed = Arrays.stream(ServiceCategory.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new IllegalArgumentException("Invalid category. Allowed: " + allowed);
        }
    }
}

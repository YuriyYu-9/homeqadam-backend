package com.homeqadam.backend.review;

import com.homeqadam.backend.order.Order;
import com.homeqadam.backend.order.OrderRepository;
import com.homeqadam.backend.order.OrderStatus;
import com.homeqadam.backend.profile.Profile;
import com.homeqadam.backend.profile.ProfileRepository;
import com.homeqadam.backend.review.dto.ReviewCreateRequest;
import com.homeqadam.backend.review.dto.ReviewResponse;
import com.homeqadam.backend.user.Role;
import com.homeqadam.backend.user.User;
import com.homeqadam.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ВАЖНО: этот импорт ОБЯЗАТЕЛЕН
import com.homeqadam.backend.review.Review;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public ReviewResponse createReview(Long clientId, Long orderId, ReviewCreateRequest req) {

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (client.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Only client can create review");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(clientId)) {
            throw new IllegalArgumentException("Not your order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Order must be completed to leave a review");
        }

        if (order.getTechnician() == null) {
            throw new IllegalArgumentException("Order has no technician");
        }

        if (reviewRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("Review for this order already exists");
        }

        String text = req.getText() == null ? null : req.getText().trim();
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text is required");
        }

        Integer rating = req.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = Review.builder()
                .order(order)
                .client(client)
                .technician(order.getTechnician())
                .rating(rating)
                .text(text)
                .build();

        review = reviewRepository.save(review);

        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByTechnician(Long technicianId) {
        return reviewRepository.findByTechnicianIdOrderByCreatedAtDesc(technicianId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review r) {

        Profile clientProfile =
                profileRepository.findByUserId(r.getClient().getId()).orElse(null);

        Profile techProfile =
                profileRepository.findByUserId(r.getTechnician().getId()).orElse(null);

        return ReviewResponse.builder()
                .id(r.getId())
                .orderId(r.getOrder().getId())

                .clientId(r.getClient().getId())
                .clientFirstName(clientProfile != null ? clientProfile.getFirstName() : null)
                .clientLastName(clientProfile != null ? clientProfile.getLastName() : null)

                .technicianId(r.getTechnician().getId())
                .technicianFirstName(techProfile != null ? techProfile.getFirstName() : null)
                .technicianLastName(techProfile != null ? techProfile.getLastName() : null)

                .rating(r.getRating())
                .text(r.getText())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

package com.homeqadam.backend.review;

import com.homeqadam.backend.review.dto.ReviewCreateRequest;
import com.homeqadam.backend.review.dto.ReviewResponse;
import com.homeqadam.backend.security.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/order/{orderId}")
    public ReviewResponse create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        Long clientId = principal.getUser().getId();
        return reviewService.createReview(clientId, orderId, request);
    }

    @GetMapping
    public List<ReviewResponse> all() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/technician/{technicianId}")
    public List<ReviewResponse> byTechnician(@PathVariable Long technicianId) {
        return reviewService.getReviewsByTechnician(technicianId);
    }
}

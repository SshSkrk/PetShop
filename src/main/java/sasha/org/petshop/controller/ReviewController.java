package sasha.org.petshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.petshop.dto.ReviewDTO;
import sasha.org.petshop.repo.ReviewRepository;
import sasha.org.petshop.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewRepository reviewRepository, ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/createReview")
    public ResponseEntity<Boolean> createReview(@RequestBody ReviewDTO reviewDTO) {
        boolean created = reviewService.createReview(reviewDTO);
        return created
                ? ResponseEntity.status(HttpStatus.CREATED).body(true)  // Created successfully
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Failed to create
    }

    @GetMapping("/admin/ListOfAllReviews")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewDTO> getReviews() {
        List<ReviewDTO> reviews = reviewService.getReviews();
        return reviews; // Return list of reviews
    }
}

package sasha.org.petshop.service;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.ReviewDTO;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.model.Order;
import sasha.org.petshop.model.Review;
import sasha.org.petshop.repo.CustomerRepository;
import sasha.org.petshop.repo.OrderRepository;
import sasha.org.petshop.repo.ReviewRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, CustomerRepository customerRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean createReview(ReviewDTO reviewDTO) {
        Order order = orderRepository.findById(reviewDTO.getOrderId()).orElseThrow(()
                -> new IllegalArgumentException("Order not found"));

        Customer customer = customerRepository.findById(reviewDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));


        if (order.getReview() != null) {
            throw new IllegalStateException("This order already has a review.");
        }

        Review reviewNew = Review.of(reviewDTO, customer);

        order.setReview(reviewNew);
        //orderRepository.save(order);
        reviewRepository.save(reviewNew);
        return true;
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviews() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            return null;
        }
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        for (Review review : reviews) {
            reviewDTOs.add(review.toReviewDTO());
        }
        return reviewDTOs;
    }
}

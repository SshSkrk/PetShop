package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.petshop.dto.ReviewDTO;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rating;

    private String reviewText;

    @OneToOne(mappedBy = "review",cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonBackReference
    private Order order;

    private Integer customerID;
    private String customerUsername;

    public Review(Integer rating, String reviewText, Order order) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.order = order;
        this.customerID = order.getCustomer().getId();
        this.customerUsername = order.getCustomer().getUsername();
    }

    public static Review of(ReviewDTO reviewDTO, Customer customer) {
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());

        for (Order order : customer.getOrders()) {
            if (order.getId().equals(reviewDTO.getOrderId())) {
                review.setOrder(order);
                break;
            }
        }

        review.setCustomerID(reviewDTO.getCustomerId());
        review.setCustomerUsername(reviewDTO.getCustomerUsername());

        return review;
    }

    public ReviewDTO toReviewDTO() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setRating(this.rating);
        reviewDTO.setReviewText(this.reviewText);

        reviewDTO.setCustomerId(this.customerID);
        reviewDTO.setCustomerUsername(this.customerUsername);
        reviewDTO.setOrderId(this.order.getId());

        return reviewDTO;
    }
}
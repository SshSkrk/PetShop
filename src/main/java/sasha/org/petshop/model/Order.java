package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.petshop.dto.OrderDTO;
import sasha.org.petshop.repo.ProductRepository;
import sasha.org.petshop.repo.ReviewRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "pet_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date orderDate;

    @Column (nullable = false)
    private String orderStatus;

    @Column (nullable = false)
    private double totalAmount;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToMany(mappedBy = "orders",  fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "review_id")
    private Review review;

    public Order(Date orderDate, String orderStatus, double totalAmount, Customer customer, List<Product> products) {
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.products = products;
        this.customer = customer;
    }

    public Order(Integer id, Date orderDate, String orderStatus, double totalAmount, Customer customer,
                 List<Product> products, Review review) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;

        this.customer = customer;

        this.products = products;
        this.review = review;
    }

    public static Order of(OrderDTO orderDTO, Customer customer,
                           ProductRepository productRepository, ReviewRepository reviewRepository) {
        Order order = new Order();
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderStatus(orderDTO.getOrderStatus());
        order.setTotalAmount(orderDTO.getTotalAmount());

        order.setCustomer(customer);

        if (orderDTO.getProductsId() != null) {
            List<Product> products = orderDTO.getProductsId().stream()
                    .map(id -> productRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found")))
                    .collect(Collectors.toList());

            order.setProducts(products);
        }

        if (orderDTO.getReviewId() != null) {
            order.setReview(reviewRepository.findById(orderDTO.getReviewId()).orElse(null));
        }

        return order;
    }

    public OrderDTO toOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(this.id);
        orderDTO.setOrderDate(this.orderDate);
        orderDTO.setOrderStatus(this.orderStatus);
        orderDTO.setTotalAmount(this.totalAmount);

        if (this.products != null) {
            orderDTO.setProductsId(this.products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList()));

            orderDTO.setProductsName(this.products.stream()
                    .map(Product::getName)
                    .collect(Collectors.toList()));

            orderDTO.setProductsPrice(this.products.stream()
                    .map(Product::getPrice)
                    .collect(Collectors.toList()));

            orderDTO.setProductsQuantity(this.products.stream()
                    .map(Product::getQuantity)
                    .collect(Collectors.toList()));
        }


        if(this.review != null) {
            orderDTO.setReviewId(this.review.getId());
        }

        orderDTO.setCustomerId(this.customer.getId());
        orderDTO.setCustomerUsername(this.customer.getUsername());

        return orderDTO;
    }

}

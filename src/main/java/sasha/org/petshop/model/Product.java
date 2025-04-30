package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.petshop.dto.ProductDTO;
import sasha.org.petshop.repo.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    private Integer quantity;

    private String imagePath;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "products_orders",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    @JsonBackReference
    private List<Order> orders = new ArrayList<>();

    public Product(Integer id, String name, double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(Integer id, String name, double price, Integer quantity, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public Product(Integer id, String name, double price, Integer quantity, String imagePath, List<Order> orders) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.orders = orders;
    }

    public static Product of(ProductDTO productDTO, OrderRepository orderRepository) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setImagePath(productDTO.getImagePath());
        if (productDTO.getOrderIds() != null && !productDTO.getOrderIds().isEmpty()) {
            List<Order> orders = productDTO.getOrderIds().stream()
                    .map(id -> orderRepository.findById(id).get()).collect(Collectors.toList());

            product.setOrders(orders);
        }

        return product;
    }

    public ProductDTO toProductDTO() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(this.id);
        productDTO.setName(this.name);
        productDTO.setPrice(this.price);
        productDTO.setQuantity(this.quantity);
        productDTO.setImagePath(this.imagePath);
        productDTO.setOrderIds(this.orders.stream().map(Order::getId).collect(Collectors.toList()));

        return productDTO;
    }
}

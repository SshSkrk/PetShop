package sasha.org.petshop.service;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.OrderDTO;
import sasha.org.petshop.dto.OrderStatusUpdateDTO;
import sasha.org.petshop.dto.ProductDTO;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.model.Order;
import sasha.org.petshop.model.Product;
import sasha.org.petshop.model.Review;
import sasha.org.petshop.repo.CustomerRepository;
import sasha.org.petshop.repo.OrderRepository;
import sasha.org.petshop.repo.ProductRepository;
import sasha.org.petshop.repo.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    public final CustomerRepository customerRepository;
    public final ProductRepository productRepository;
    public final ReviewRepository reviewRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public boolean createOrder(OrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Order order = new Order();
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderStatus(orderDTO.getOrderStatus());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setCustomer(customer);

        order = orderRepository.save(order);

        // Link Order to Customer
        if (customer.getOrders() == null) {
            customer.setOrders(new ArrayList<>());
        }
        customer.getOrders().add(order);

        //customerRepository.save(customer);


        // Link Products to Order with updated quantities
        List<Product> productsToOrder = new ArrayList<>();
        List<Integer> productIds = orderDTO.getProductsId();
        List<Integer> quantitiesFromDTO = orderDTO.getProductsQuantity();

        for (int i = 0; i <productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Product not found" ));

            product.setQuantity(quantitiesFromDTO.get(i));
            // Link Order to Product
            if (product.getOrders() == null) {
                product.setOrders(new ArrayList<>());
            }
            product.getOrders().add(order);
            //productRepository.save(product);
            productsToOrder.add(product);
        }

        order.setProducts(productsToOrder);


        // Connect Review (if provided)
        if (orderDTO.getReviewId() != null) {
            Review review = reviewRepository.findById(orderDTO.getReviewId()).orElse(null);
            if (review != null) {
                review.setOrder(order);
                //reviewRepository.save(review);
                order.setReview(review);
            }
        }

        orderRepository.save(order);

        return true;
    }

    @Transactional
    public boolean updateOrder(OrderStatusUpdateDTO orderStatusUpdateDTO) {
        var orderOptional = orderRepository.findById(orderStatusUpdateDTO.getId());
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setOrderStatus(orderStatusUpdateDTO.getOrderStatus());
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            orderDTOs.add(order.toOrderDTO());
        }
        return orderDTOs;
    }

    @Transactional
    public boolean deleteOrder(Integer id) {
        var orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();


            //detach Customer to Order
            Customer customer = (Customer) customerRepository.getCustomerById(order.getCustomer().getId());
            customer.getOrders().remove(order);
            customerRepository.save(customer);
            order.setCustomer(null);

            // detach Products to Order
            List<Product> products = order.getProducts();
            for (Product product : products) {
                product.getOrders().remove(order);
                productRepository.save(product);
            }
            order.setProducts(null);

            // detach Review (if provided)
            if (order.getReview() != null) {
                Review review = order.getReview();
                review.setOrder(null);
                reviewRepository.save(review);
                order.setReview(null);
            }

            orderRepository.delete(order);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> ordersByCustomer(String username) {
        List<Order> orders = orderRepository.findAllByCustomer_Username(username);
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            orderDTOs.add(order.toOrderDTO());
        }
        return orderDTOs;
    }
}

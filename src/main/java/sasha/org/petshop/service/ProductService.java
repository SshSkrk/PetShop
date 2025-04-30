package sasha.org.petshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.OrderDTO;
import sasha.org.petshop.dto.ProductDTO;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.model.Order;
import sasha.org.petshop.model.Product;
import sasha.org.petshop.repo.CustomerRepository;
import sasha.org.petshop.repo.OrderRepository;
import sasha.org.petshop.repo.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean createProduct(ProductDTO productDTO) {
        productRepository.save(Product.of(productDTO, orderRepository));
        return true;
    }

    @Transactional
    public boolean updateProduct(ProductDTO productDTO) {
        var productOptional = productRepository.findById(productDTO.getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            product.setPrice(productDTO.getPrice());
            product.setName(productDTO.getName());

            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            productDTOs.add(product.toProductDTO());
        }
        return productDTOs;
    }

    @Transactional
    public boolean deleteProductById(Integer id) {
        var productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Create and save the fake "???" product
            Product fakeProduct = new Product();
            fakeProduct.setName("???");
            fakeProduct.setPrice(product.getPrice());
            fakeProduct.setQuantity(product.getQuantity());
            fakeProduct.setImagePath(product.getImagePath());
            fakeProduct.setOrders(product.getOrders());

            Product fakeProductSaved = productRepository.save(fakeProduct);

            // Replace the old product with "???" in all associated orders
            product.getOrders().forEach(order -> {
                order.setProducts(order.getProducts().stream()
                                .map(p -> p.getId().equals(product.getId()) ? fakeProductSaved : p)
                                .collect(Collectors.toList())
                );
                orderRepository.save(order);
            });

            productRepository.delete(product);

            return true;
        }
        return false;
    }
}

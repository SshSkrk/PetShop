package sasha.org.petshop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sasha.org.petshop.dto.CustomerDTO;
import sasha.org.petshop.dto.ProductDTO;
import sasha.org.petshop.repo.CustomerRepository;
import sasha.org.petshop.repo.OrderRepository;
import sasha.org.petshop.repo.ProductRepository;
import sasha.org.petshop.service.CustomerService;
import sasha.org.petshop.service.OrderService;
import sasha.org.petshop.service.ProductService;
import sasha.org.petshop.service.ReviewService;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(
                                  PasswordEncoder encoder,
                                  final CustomerService customerService,
                                  final ProductService productService,
                                  final OrderService orderService,
                                  final ReviewService reviewService,
                                  final CustomerRepository customerRepository,
                                  final ProductRepository productRepository,
                                  final OrderRepository orderRepository) {
        return strings -> {
            CustomerDTO customerDTOADMIN = new CustomerDTO();
            customerDTOADMIN.setFirstName("Oleksandra");
            customerDTOADMIN.setLastName("Skoryk");
            customerDTOADMIN.setUsername("OS");
            customerDTOADMIN.setRole("ADMIN"); //USER
            customerDTOADMIN.setPassword("123456");
            customerService.createCustomer(customerDTOADMIN);

            CustomerDTO customerDTOUSER = new CustomerDTO();
            customerDTOUSER.setFirstName("Anna");
            customerDTOUSER.setLastName("Bidenko");
            customerDTOUSER.setUsername("AB");
            customerDTOUSER.setRole("USER");
            customerDTOUSER.setPassword("123456");
            customerService.createCustomer(customerDTOUSER);



            ProductDTO productDTO1 = new ProductDTO();
            productDTO1.setName("Feed");
            productDTO1.setPrice(200.0);
            productDTO1.setImagePath("/pic/feed.jpg");
            productService.createProduct(productDTO1);

            ProductDTO productDTO2 = new ProductDTO();
            productDTO2.setName("Hay");
            productDTO2.setPrice(50.0);
            productDTO2.setImagePath("/pic/hay.jpg");
            productService.createProduct(productDTO2);

            ProductDTO productDTO3 = new ProductDTO();
            productDTO3.setName("Canned food");
            productDTO3.setPrice(25.0);
            productDTO3.setImagePath("/pic/canned_food.jpg");
            productService.createProduct(productDTO3);

        };
    }
}

package sasha.org.petshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecConfig {

    private final UserDetailsService userDetailsServiceImpl;
    private final PasswordEncoder encoder;

    public SecConfig(UserDetailsService userDetailsServiceImpl, PasswordEncoder encoder) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.encoder = encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Allow session creation
                )

                .authorizeHttpRequests(auth -> auth

                .requestMatchers("/", "/index.html", "/register.html", "/login.html", "/cart.html",
                        "/products.html","/css/**", "/js/**", "/pic/**").permitAll()
                .requestMatchers("/admin.html").hasAnyRole("ADMIN")
                .requestMatchers("/profile.html", "/review.html").hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/login", "/api/logout", "/api/current-user").permitAll()

                .requestMatchers("/api/").permitAll()
                .requestMatchers("/api/registrationOfNewCustomer").permitAll()

                .requestMatchers("/api/customerUpdateOnly").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/customerInfo/updateAddress").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/customerInfo/updateContacts").hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/order/createOrder").hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/ListOfAllProducts").permitAll()

                .requestMatchers("/api/createReview").hasAnyRole("USER", "ADMIN")

                // Admin related actions - ROLE_ADMIN
                .requestMatchers("/api/admin/getCustomerByUsername").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/ListOfAllCustomers").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/deleteCustomer/{id}").hasAnyRole("ADMIN")

                .requestMatchers("/api/admin/updateOrder").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/ordersByCustomer").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/ListOfAllOrders").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/deleteOrder/{id}").hasAnyRole("ADMIN")

                .requestMatchers("/api/admin/createProduct").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/updateProduct").hasAnyRole("ADMIN")
                .requestMatchers("/api/admin/deleteProduct/{id}").hasAnyRole("ADMIN")

                .requestMatchers("/api/admin/ListOfAllReviews").hasAnyRole("ADMIN")

                .anyRequest().authenticated()
                );

        return http.build();
    }
}
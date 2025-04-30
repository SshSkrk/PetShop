package sasha.org.petshop.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.repo.CustomerRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public UserDetailsServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Customer customer = (Customer) customerRepository.getCustomersByUsername(username);

        if (customer == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword()) // bcrypt-encoded in DB
                .authorities("ROLE_" + customer.getRole()) // "ROLE_ADMIN", etc.
                .build();
    }
}

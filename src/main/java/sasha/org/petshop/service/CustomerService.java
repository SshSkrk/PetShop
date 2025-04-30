package sasha.org.petshop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.CustomerDTO;
import sasha.org.petshop.dto.OrderDTO;
import sasha.org.petshop.model.*;
import sasha.org.petshop.repo.AddressRepository;
import sasha.org.petshop.repo.ContactsRepository;
import sasha.org.petshop.repo.CustomerRepository;
import sasha.org.petshop.repo.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder encoder;
    private final AddressRepository addressRepository;
    private final ContactsRepository contactsRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder encoder,
                           AddressRepository addressRepository,
                           ContactsRepository contactsRepository,
                           OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.encoder = encoder;
        this.addressRepository = addressRepository;
        this.contactsRepository = contactsRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean createCustomer(CustomerDTO customerDTO) {
        var customerVar = customerRepository.getCustomersByUsername(customerDTO.getUsername());
        if (customerVar == null) {
            Customer customerNew = Customer.of(customerDTO,addressRepository, contactsRepository, orderRepository);
            customerNew.setPassword(encoder.encode(customerDTO.getPassword()));
            customerRepository.save(customerNew);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateCustomer(CustomerDTO customerDTO) {
        var customerVar = customerRepository.getCustomersByUsername(customerDTO.getUsername());
        if (customerVar != null) {
            Customer customer = (Customer) customerVar;
            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());
            customer.setUsername(customerDTO.getUsername());
            customer.setRole(customerDTO.getRole());

            // Only encode if it's not already encoded (BCrypt starts with "$2a$")
            String incomingPassword = customerDTO.getPassword();
            if (!incomingPassword.startsWith("$2a$")) {
                customer.setPassword(encoder.encode(incomingPassword));
            } else {
                customer.setPassword(incomingPassword); // already encoded
            }

            if (customerDTO.getContactsId() != null) {
                Contacts contacts = contactsRepository.findById(customerDTO.getContactsId()).orElse(null);
                customer.setContacts(contacts);
                if (contacts != null) {
                    contacts.setCustomer(customer);
                    //contactsRepository.save(contacts);
                }
            }
            if (customerDTO.getAddressId() != null) {
                Address address = addressRepository.findById(customerDTO.getAddressId()).orElse(null);
                customer.setAddress(address);
                if (address != null) {
                    address.setCustomer(customer);
                    //addressRepository.save(address);
                }
            }
            if (customerDTO.getOrdersId() != null && !customerDTO.getOrdersId().isEmpty()) {
                List<Order> orders = customerDTO.getOrdersId().stream()
                        .map(id -> orderRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .peek(order -> {
                            order.setCustomer(customer);
                            //orderRepository.save(order);
                        })
                        .collect(Collectors.toList());
                customer.setOrders(orders);
            }


            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByUsername(String username) {
        var customerVar = customerRepository.getCustomersByUsername(username);
        if (customerVar == null) {
            return null;
        }
        Customer customer = (Customer) customerVar;
        CustomerDTO customerDTO = customer.toCustomerDTO();
        return customerDTO;
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Integer customerId) {
        var customerVar = customerRepository.getCustomerById(customerId);
        if (customerVar == null) {
            return null;
        }
        Customer customer = (Customer) customerVar;
        CustomerDTO customerDTO = customer.toCustomerDTO();
        return customerDTO;
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            return null;
        }
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        for (Customer customer : customers) {
            customerDTOs.add(customer.toCustomerDTO());
        }
        return customerDTOs;
    }

    @Transactional
    public boolean deleteCustomer(Integer id) {
        var customerVar = customerRepository.getCustomerById(id);
        if (customerVar != null) {
            Customer customer = (Customer) customerVar;

            //detach
            if (customer.getContacts() != null) { // could be empty Contacts
                Contacts contacts = contactsRepository.findById(customer.getContacts().getId()).orElse(null);
                customer.setContacts(null);
                if (contacts != null) {  // <- double safety check
                    contactsRepository.delete(contacts);
                }
            }
            if (customer.getAddress() != null) { // could be empty Address
                Address address = addressRepository.findById(customer.getAddress().getId()).orElse(null);
                customer.setAddress(null);
                if (address != null) {  // <- double safety check
                    addressRepository.delete(address);
                }
            }
            if (customer.getOrders() != null && !customer.getOrders().isEmpty()) {
                List<Order> orders = customer.getOrders();
                for (Order order : orders) {
                    // Detach products
                    if (order.getProducts() != null) {
                        for (Product product : order.getProducts()) {
                            product.getOrders().remove(order); // bidirectional
                        }
                        order.setProducts(null);
                    }

                    // Detach customer
                    order.setCustomer(null);

                    // Delete order
                    orderRepository.delete(order);
                }
                customer.setOrders(null);
            }

            // Finally delete customer
            customerRepository.delete(customer);
            return true;
        }
        return false;
    }
}

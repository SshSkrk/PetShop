package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sasha.org.petshop.dto.*;
import sasha.org.petshop.repo.AddressRepository;
import sasha.org.petshop.repo.ContactsRepository;
import sasha.org.petshop.repo.OrderRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude = {"contacts", "address", "orders"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "contacts_id")
    @JsonManagedReference
    private Contacts contacts;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    @JsonManagedReference
    private Address address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders;


    public Customer(String firstName, String lastName, String username, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public Customer(Integer id ,String firstName, String lastName, String username, String role, String password,
                    Contacts contacts, Address address, List<Order> orders){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
        this.password = password;
        this.contacts = contacts;
        this.address = address;
        this.orders = orders;
    }

    public static Customer of(CustomerDTO customerDTO, AddressRepository addressRepository,
                              ContactsRepository contactsRepository,
                              OrderRepository orderRepository) {
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setUsername(customerDTO.getUsername());
        customer.setRole(customerDTO.getRole());
        customer.setPassword(customerDTO.getPassword());

        if (customerDTO.getAddressId() != null) {
            customer.setAddress(addressRepository.findById(customerDTO.getAddressId()).orElse(null));
        } else {
            Address newAddress = new Address();
            newAddress.setAddress("");
            newAddress.setCity("");
            newAddress.setCountry("");
            addressRepository.save(newAddress);
            customer.setAddress(newAddress);
        }

        if (customerDTO.getContactsId() != null) {
            customer.setContacts(contactsRepository.findById(customerDTO.getContactsId()).orElse(null));
        } else {
            Contacts newContacts = new Contacts();
            newContacts.setEmail(UUID.randomUUID().toString() + "@temp");
            newContacts.setPhone("000-" + UUID.randomUUID().toString().substring(0, 6));
            contactsRepository.save(newContacts);
            customer.setContacts(newContacts);
        }

        if (customerDTO.getOrdersId() != null && !customerDTO.getOrdersId().isEmpty()) {
            List<Order> orders = orderRepository.findAllById(customerDTO.getOrdersId());
            customer.setOrders(orders);
        } else {
            customer.setOrders(new ArrayList<>());
        }

        return customer;
    }

    public CustomerDTO toCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(this.id);
        customerDTO.setFirstName(this.firstName);
        customerDTO.setLastName(this.lastName);
        customerDTO.setUsername(this.username);
        customerDTO.setRole(this.role);
        customerDTO.setPassword(this.password);
        if (this.contacts != null) {
            customerDTO.setContactsId(this.contacts.getId());
        }
        if (this.address != null) {
            customerDTO.setAddressId(this.address.getId());
        }
        if (this.orders != null) {
            customerDTO.setOrdersId(this.orders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toList()));
        }
        return customerDTO;
    }
}
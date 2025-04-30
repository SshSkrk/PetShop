package sasha.org.petshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.petshop.dto.AddressDTO;
import sasha.org.petshop.dto.ContactsDTO;
import sasha.org.petshop.dto.CustomerDTO;
import sasha.org.petshop.model.Address;
import sasha.org.petshop.service.AddressService;
import sasha.org.petshop.service.ContactsService;
import sasha.org.petshop.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ContactsService contactsService;

    public CustomerController(CustomerService customerService, AddressService addressService, ContactsService contactsService) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.contactsService = contactsService;
    }

    @PostMapping(value = "/registrationOfNewCustomer", consumes = "application/json")
    public ResponseEntity<Boolean> createCustomer(@RequestBody CustomerDTO customerDTO) {
        boolean created = customerService.createCustomer(customerDTO);
        return created
                ? ResponseEntity.status(HttpStatus.CREATED).body(true)  // Created successfully
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Failed to create
    }

    @PostMapping("/customerUpdateOnly")
    public ResponseEntity<Boolean> updateCustomerInfo(@RequestBody CustomerDTO customerDTO) {
        boolean updated = customerService.updateCustomer(customerDTO);
        return updated
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    @PostMapping("/customerInfo/updateAddress")
    public ResponseEntity<Boolean> updateAddressInfo(@RequestBody AddressDTO addressDTO) {
        if (addressDTO.getCustomerId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        } else {
            boolean updated = addressService.updateAddress(addressDTO);
            return updated
                    ? ResponseEntity.ok(true)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }

    @PostMapping("/customerInfo/updateContacts")
    public ResponseEntity<Boolean> updateContactsInfo(@RequestBody ContactsDTO contactsDTO) {
        if (contactsDTO.getCustomerId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        } else {
            boolean updated = contactsService.updateContacts(contactsDTO);
            return updated
                    ? ResponseEntity.ok(true)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }

    @GetMapping("/admin/getCustomerByUsername/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDTO getCustomerByLogin(@PathVariable String username) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return customer; // Return customer
    }

    @GetMapping("/admin/ListOfAllCustomers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return customers; // Return list of products
    }

    @DeleteMapping("/admin/deleteCustomer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteCustomer(@PathVariable("id") Integer id) {
        boolean deleted = customerService.deleteCustomer(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)  // Deleted successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Customer not found to delete
    }

    @GetMapping("/customerInfo/getAddress/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public AddressDTO getAddress(@PathVariable("id") Integer id) {
        AddressDTO addresses = addressService.getAddress(id);
        return addresses;
    }

    @GetMapping("/customerInfo/getContacts/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ContactsDTO getContacts(@PathVariable("id") Integer id) {
        ContactsDTO contacts = contactsService.getContacts(id);
        return contacts;
    }

    @GetMapping("/customerInfo/getCustomerById/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public CustomerDTO getCustomerById(@PathVariable("id") Integer id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return customer;
    }
}
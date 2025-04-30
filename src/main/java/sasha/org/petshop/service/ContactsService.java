package sasha.org.petshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.petshop.dto.ContactsDTO;
import sasha.org.petshop.dto.CustomerDTO;
import sasha.org.petshop.model.Contacts;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.repo.ContactsRepository;
import sasha.org.petshop.repo.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactsService {
    private final ContactsRepository contactsRepository;
    private final CustomerRepository customerRepository;

    public ContactsService(ContactsRepository contactsRepository, CustomerRepository customerRepository) {
        this.contactsRepository = contactsRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public boolean updateContacts(ContactsDTO contactsDTO) {
        var contactsVar = contactsRepository.getContactsByCustomerId(contactsDTO.getCustomerId());

        Customer customer = customerRepository.findById(contactsDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (contactsVar == null) {
            Contacts contactsNew = Contacts.of(contactsDTO, customer);
            //contactsRepository.save(contactsNew);

            customer.setContacts(contactsNew);     // link contacts to customer
            customerRepository.save(customer);
            return true;
        } else if (contactsVar != null) {
            Contacts contacts = (Contacts) contactsVar;
            contacts.setEmail(contactsDTO.getEmail());
            contacts.setPhone(contactsDTO.getPhone());
            contacts.setCustomer(customer);
            //contactsRepository.save(contacts);

            customer.setContacts(contacts);     // link contacts to customer
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public ContactsDTO getContacts(Integer customerId) {
        var customerVar = customerRepository.getCustomerById(customerId);
        if (customerVar != null) {
            Customer customer = (Customer) customerVar;
            if (customer.getContacts() != null) {
                Contacts contacts = customer.getContacts();
                ContactsDTO contactsDTO = contacts.toContactsDTO();
                return contactsDTO;
            }
        }
        return null;
    }
}
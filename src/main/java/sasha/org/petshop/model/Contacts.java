package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sasha.org.petshop.dto.ContactsDTO;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude = "customer")
public class Contacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @OneToOne(mappedBy = "contacts", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonBackReference
    private Customer customer;

    public Contacts(String email, String phone, Customer customer) {
        this.email = email;
        this.phone = phone;

        this.customer = customer;
    }

    public static Contacts of(ContactsDTO contactsDTO, Customer customer) {
        Contacts contacts = new Contacts();
        contacts.setId(contactsDTO.getId());
        contacts.setEmail(contactsDTO.getEmail());
        contacts.setPhone(contactsDTO.getPhone());

        contacts.setCustomer(customer);

        return contacts;
    }

    public ContactsDTO toContactsDTO() {
        ContactsDTO contactsDTO = new ContactsDTO();
        contactsDTO.setId(id);
        contactsDTO.setEmail(this.email);
        contactsDTO.setPhone(this.phone);

        contactsDTO.setCustomerId(this.customer.getId());
        contactsDTO.setCustomerUsername(this.customer.getUsername());

        return contactsDTO;
    }
}

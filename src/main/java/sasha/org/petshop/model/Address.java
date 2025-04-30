package sasha.org.petshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sasha.org.petshop.dto.AddressDTO;

@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude = "customer")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String address;

    private String city;

    private String country;

    @OneToOne(mappedBy = "address", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonBackReference
    private Customer customer;

    public Address(String address, String city, String country, Customer customer) {
        this.address = address;
        this.city = city;
        this.country = country;

        this.customer = customer;
    }

    public static Address of(AddressDTO addressDTO, Customer customer) {
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setAddress(addressDTO.getAddress());
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());

        address.setCustomer(customer);

        return address;
    }

    public AddressDTO toAddressDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(this.getId());
        addressDTO.setAddress(this.address);
        addressDTO.setCity(this.city);
        addressDTO.setCountry(this.country);

        addressDTO.setCustomerId(this.customer.getId());
        addressDTO.setCustomerUsername(this.customer.getUsername());

        return addressDTO;
    }
}

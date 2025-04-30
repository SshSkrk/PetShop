package sasha.org.petshop.dto;

import lombok.Data;
import sasha.org.petshop.model.Customer;

@Data
public class AddressDTO {
    private Integer id;
    private String address;
    private String city;
    private String country;
    private Integer customerId;
    private String customerUsername;

}

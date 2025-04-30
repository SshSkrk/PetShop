package sasha.org.petshop.dto;

import lombok.Data;
import sasha.org.petshop.model.Customer;

@Data
public class ContactsDTO {
    private Integer id;
    private String email;
    private String phone;
    private Integer customerId;
    private String customerUsername;

}

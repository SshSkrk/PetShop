package sasha.org.petshop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import sasha.org.petshop.model.Address;
import sasha.org.petshop.model.Contacts;
import sasha.org.petshop.model.Order;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String password;
    private Integer contactsId;
    private Integer addressId;
    private List<Integer> ordersId;

}

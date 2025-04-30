package sasha.org.petshop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import sasha.org.petshop.model.Customer;
import sasha.org.petshop.model.Product;
import sasha.org.petshop.model.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderDTO {
    private Integer id;
    private Date orderDate;
    private String orderStatus;
    private double totalAmount;

    private List<Integer> productsId;
    private List<String> productsName;
    private List<Double> productsPrice;
    private List<Integer> productsQuantity;
    private Integer reviewId;

    private Integer customerId;
    private String customerUsername;
}

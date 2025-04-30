package sasha.org.petshop.dto;

import lombok.Data;
import sasha.org.petshop.model.Order;
import sasha.org.petshop.model.Review;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private double price;
    private Integer quantity;
    private String imagePath;
    private List<Integer> orderIds;

}

package sasha.org.petshop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import sasha.org.petshop.model.Order;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReviewDTO {

    private Integer rating;
    private String reviewText;

    private Integer orderId;
    private Integer customerId;
    private String customerUsername;

}

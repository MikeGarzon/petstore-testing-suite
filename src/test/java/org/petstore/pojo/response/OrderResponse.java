package org.petstore.pojo.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private Long petId;
    private Integer quantity;
    private String shipDate;  // ISO format date string
    private String status;    // placed, approved, delivered
    private Boolean complete;
}
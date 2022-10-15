package labshopeventuate.domain;

import labshopeventuate.domain.*;
import lombok.*;
import java.util.*;

import io.eventuate.Event;

@Data
@ToString
public class OrderPlaced implements Event {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
    private String status;
    private String address;
}



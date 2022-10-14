package labshopeventuate.domain;

import labshopeventuate.domain.*;
//import labshopeventuate.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class OrderCancelled {//extends AbstractEvent {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
    private String status;
    private String address;

    public OrderCancelled(Order aggregate){
        //super(aggregate);
    }
    public OrderCancelled(){
        super();
    }
}

package labshopeventuate.domain;

import labshopeventuate.domain.*;
//import labshopeventuate.infra.AbstractEvent;
import java.util.*;

import io.eventuate.Event;
import lombok.*;

@Data
@ToString
public class OrderCancelled implements Event{//extends AbstractEvent {

 //   private String id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
    private String status;
    private String address;

}

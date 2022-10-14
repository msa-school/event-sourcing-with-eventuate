package labshopeventuate.domain;

import labshopeventuate.domain.*;
//import labshopeventuate.infra.AbstractEvent;
import java.util.*;

import org.springframework.beans.BeanUtils;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.*;

@Data
@ToString
public class OrderPlaced implements DomainEvent{//extends AbstractEvent {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
    private String status;
    private String address;

    public OrderPlaced(Order aggregate){
       BeanUtils.copyProperties(aggregate, this);
    }
    public OrderPlaced(){
        super();
    }
}

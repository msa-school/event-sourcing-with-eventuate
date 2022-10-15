package labshopeventuate.domain;

import javax.persistence.criteria.Order;

import lombok.Data;

@Data
public class PlaceOrderCommand implements OrderCommand{

    int qty;
    String proudctId;
    
}

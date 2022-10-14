package labshopeventuate.domain;

import javax.persistence.criteria.Order;

public class PlaceOrderCommand implements OrderCommand{

    int qty;
    String itemId;
    
}

package labshopeventuate.domain;

import labshopeventuate.domain.OrderPlaced;
import labshopeventuate.domain.OrderCancelled;
import labshopeventuate.OrderApplication;

import java.util.List;

import org.springframework.beans.BeanUtils;

import io.eventuate.Event;
import io.eventuate.ReflectiveMutableCommandProcessingAggregate;
import lombok.Data;

import java.util.Collections;
import java.util.Date;

import static io.eventuate.EventUtil.events;

@Data
public class Order extends ReflectiveMutableCommandProcessingAggregate<Order, OrderCommand> {

    
    private Long id;
    
    private String productId;
    
    private Integer qty;
    
    private String customerId;
    
    private Double amount;
    
    private String status;
    
    private String address;



    public List<Event> process(PlaceOrderCommand cmd) {
        OrderPlaced orderPlaced = new OrderPlaced();
        BeanUtils.copyProperties(cmd, orderPlaced);

        return events(orderPlaced);
    }
    
    public void apply(OrderPlaced event) {
        BeanUtils.copyProperties(event, this);
    }
    


}

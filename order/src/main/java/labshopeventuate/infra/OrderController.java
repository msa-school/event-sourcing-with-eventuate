package labshopeventuate.infra;
import labshopeventuate.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.sync.AggregateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;

@RestController
//@RequestMapping(value="/orders")
@Transactional
public class OrderController {
    @Autowired
    AggregateRepository<Order, OrderCommand> orderRepository;


    @RequestMapping(method=RequestMethod.POST, path = "/orders")
    public Order placeOrder(@RequestBody PlaceOrderCommand command){
    
        return aggregateWithId(orderRepository.save(command));
    }

    @RequestMapping(method=RequestMethod.DELETE, path = "/orders/{id}")
    public Order placeOrder(@PathVariable("id") String id){
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand();
        
        return aggregateWithId(orderRepository.update(id, cancelOrderCommand));
    }

    @RequestMapping(method=RequestMethod.GET, path = "/orders/{id}")
    public Order getOrder(@PathVariable("id") String id){
        Order order = orderRepository.find(id).getEntity();
        order.setId(id);

        return order;
    }

    public Order aggregateWithId(EntityWithIdAndVersion<Order> result){
        Order order = result.getAggregate();
        order.setId(result.getEntityId());

        return order;
    }

}

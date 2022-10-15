package labshopeventuate.infra;
import labshopeventuate.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.sync.AggregateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
//@RequestMapping(value="/orders")
@Transactional
public class OrderController {
    @Autowired
    AggregateRepository<Order, OrderCommand> orderRepository;


    @RequestMapping(method=RequestMethod.POST, path = "/orders/")
    public Order placeOrder(@RequestBody PlaceOrderCommand command){
        
        EntityWithIdAndVersion<Order> result = orderRepository.save(command);
        Order order = result.getAggregate();

        order.setId(result.getEntityId());

        return order;
    }

    @RequestMapping(method=RequestMethod.DELETE, path = "/orders/{id}")
    public Order placeOrder(@PathVariable("id") String id){
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand();
        
        return orderRepository.update(id, cancelOrderCommand).getAggregate();
    }

    @RequestMapping(method=RequestMethod.GET, path = "/orders/{id}")
    public Order getOrder(@PathVariable("id") String id){
        return orderRepository.find(id).getEntity();
    }

}

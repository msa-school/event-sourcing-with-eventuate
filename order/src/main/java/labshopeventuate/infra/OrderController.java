package labshopeventuate.infra;
import labshopeventuate.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.eventuate.sync.AggregateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
// @RequestMapping(value="/orders")
@Transactional
public class OrderController {
    @Autowired
    AggregateRepository<Order, OrderCommand> orderRepository;


    @RequestMapping(method=RequestMethod.POST, name = "/orders")
    public Order placeOrder(@RequestBody PlaceOrderCommand command){
        
        return orderRepository.save(command).getAggregate();
    }

    @RequestMapping(method=RequestMethod.DELETE, name = "/orders/{id}")
    public Order placeOrder(@PathVariable("id") String id){
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand();
        
        return orderRepository.update(id, cancelOrderCommand).getAggregate();
    }

    @RequestMapping(method=RequestMethod.GET, name = "/orders/{id}")
    public Order getOrder(@PathVariable("id") String id){
        return orderRepository.find(id).getEntity();
    }

}

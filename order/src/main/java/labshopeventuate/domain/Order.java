package labshopeventuate.domain;

import labshopeventuate.domain.OrderPlaced;
import labshopeventuate.domain.OrderCancelled;
import labshopeventuate.OrderApplication;
import javax.persistence.*;

import io.eventuate.tram.events.publisher.DomainEventPublisher;

import java.util.List;
import lombok.Data;

import java.util.Collections;
import java.util.Date;

@Entity
@Table(name="Order_table")
@Data

public class Order  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String productId;
    
    private Integer qty;
    
    private String customerId;
    
    private Double amount;
    
    private String status;
    
    private String address;

    @PostPersist
    public void onPostPersist(){
        OrderPlaced orderPlaced = new OrderPlaced(this);

        DomainEventPublisher publisher = OrderApplication.applicationContext.getBean(DomainEventPublisher.class);
        publisher.publish(getClass(), getId(), Collections.singletonList(orderPlaced));
    }

    @PrePersist
    public void onPrePersist(){
        // Get request from Inventory
        //labshopeventuate.external.Inventory inventory =
        //    Application.applicationContext.getBean(labshopeventuate.external.InventoryService.class)
        //    .getInventory(/** mapping value needed */);

    }
    @PreRemove
    public void onPreRemove(){

        OrderCancelled orderCancelled = new OrderCancelled(this);

    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }






}

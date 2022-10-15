# Event Sourcing and Microservice Choreography with Eventuate Local

## How to run

- firstly install and run infrastructure including the kafka, mysql, eventuate cdc
```
cd kafka
docker-compose up
```

- run each microservice
```
cd order
mvn spring-boot:run

#another terminal
cd inventory
mvn spring-boot:run
```

- test the service:

register a product with stock 10 and create an order for the product:
```
http :8082/inventories id="TV" stock=10
http :8081/orders productId=1 qty=5
```

check the stock remaining:
```
http :8082/inventories/TV    #stock must be 5
```

- check the database:

```
docker exec -it kafka-mysql-1 /bin/bash

mysql --user=root --password=$MYSQL_ROOT_PASSWORD
use eventuate;
```

- you can see data details:
```

show tables;
+---------------------+
| Tables_in_eventuate |
+---------------------+
| cdc_monitoring      |
| entities            |
| events              |
| message             |
| offset_store        |
| received_messages   |
| snapshots           |
+---------------------+
7 rows in set (0.00 sec)


select * from events;

+-----------------------------------+--------------------------------------------+------------------------------+-----------------------------------+-----------------------------------+--------------------------------------------------------------------------+----------+-----------+
| event_id                          | event_type                                 | event_data                   | entity_type                       | entity_id                         | triggering_event                                                         | metadata | published |
+-----------------------------------+--------------------------------------------+------------------------------+-----------------------------------+-----------------------------------+--------------------------------------------------------------------------+----------+-----------+
| 00000183d9897265-0297b73e78ee0000 | labshopeventuate.domain.InventoryCreated   | {"stock":100}                | labshopeventuate.domain.Inventory | LCDT                              | NULL                                                                     | NULL     |         0 |
| 00000183d9898b4e-0297b73e78ee0000 | labshopeventuate.domain.OrderPlaced        | {"qty":5,"productId":"LCDT"} | labshopeventuate.domain.Order     | 00000183d9898b4e-0297b73e78ee0000 | NULL                                                                     | NULL     |         0 |
| 00000183d989a70c-0297b73e78ee0000 | labshopeventuate.domain.InventoryDecreased | {"qty":5}                    | labshopeventuate.domain.Inventory | LCDT                              | etpo:00000183d9898b4e-0297b73e78ee0000:labshopeventuate.domain.Order:0:0 | NULL     |         0 |
| 00000183d98a0a22-0297b73e78ee0000 | labshopeventuate.domain.OrderPlaced        | {"qty":5,"productId":"LCDT"} | labshopeventuate.domain.Order     | 00000183d98a0a22-0297b73e78ee0000 | NULL                                                                     | NULL     |         0 |
| 00000183d98ad43b-0297b73e78ee0000 | labshopeventuate.domain.InventoryDecreased | {"qty":5}                    | labshopeventuate.domain.Inventory | LCDT                              | etpo:00000183d98a0a22-0297b73e78ee0000:labshopeventuate.domain.Order:0:1 | NULL     |         0 |
+-----------------------------------+--------------------------------------------+------------------------------+-----------------------------------+-----------------------------------+--------------------------------------------------------------------------+----------+-----------+
5 rows in set (0.00 sec)



select * from entities;
+-----------------------------------+-----------------------------------+-----------------------------------+
| entity_type                       | entity_id                         | entity_version                    |
+-----------------------------------+-----------------------------------+-----------------------------------+
| labshopeventuate.domain.Inventory | LCDT                              | 00000183d98ad43b-0297b73e78ee0000 |
| labshopeventuate.domain.Order     | 00000183d9898b4e-0297b73e78ee0000 | 00000183d9898b4e-0297b73e78ee0000 |
| labshopeventuate.domain.Order     | 00000183d98a0a22-0297b73e78ee0000 | 00000183d98a0a22-0297b73e78ee0000 |
+-----------------------------------+-----------------------------------+-----------------------------------+
3 rows in set (0.00 sec)



```

you can find the kafka log:

```
docker exec -it kafka-kafka-1 /bin/bash
cd /bin

kafka-topics --bootstrap-server=localhost:9092 --list

__consumer_offsets
labshopeventuate.domain.Inventory
labshopeventuate.domain.Order
offset.storage.topic


kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Order --from-beginning

{"id":"00000183d9898b4e-0297b73e78ee0000","entityId":"00000183d9898b4e-0297b73e78ee0000","entityType":"labshopeventuate.domain.Order","eventData":"{\"qty\":5,\"productId\":\"LCDT\"}","eventType":"labshopeventuate.domain.OrderPlaced","binlogFileOffset":{"binlogFilename":"mysql-bin.000003","offset":2960,"rowsToSkip":0}}
{"id":"00000183d98a0a22-0297b73e78ee0000","entityId":"00000183d98a0a22-0297b73e78ee0000","entityType":"labshopeventuate.domain.Order","eventData":"{\"qty\":5,\"productId\":\"LCDT\"}","eventType":"labshopeventuate.domain.OrderPlaced","binlogFileOffset":{"binlogFilename":"mysql-bin.000003","offset":5484,"rowsToSkip":0}}



kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Inventory --from-beginning

{"id":"00000183d9897265-0297b73e78ee0000","entityId":"LCDT","entityType":"labshopeventuate.domain.Inventory","eventData":"{\"stock\":100}","eventType":"labshopeventuate.domain.InventoryCreated","binlogFileOffset":{"binlogFilename":"mysql-bin.000003","offset":1946,"rowsToSkip":0}}
{"id":"00000183d989a70c-0297b73e78ee0000","entityId":"LCDT","entityType":"labshopeventuate.domain.Inventory","eventData":"{\"qty\":5}","eventType":"labshopeventuate.domain.InventoryDecreased","binlogFileOffset":{"binlogFilename":"mysql-bin.000003","offset":3686,"rowsToSkip":0}}
{"id":"00000183d98ad43b-0297b73e78ee0000","entityId":"LCDT","entityType":"labshopeventuate.domain.Inventory","eventData":"{\"qty\":5}","eventType":"labshopeventuate.domain.InventoryDecreased","binlogFileOffset":{"binlogFilename":"mysql-bin.000003","offset":7970,"rowsToSkip":0}}
```



## Implementation Details

- Aggregate "Order.java" patches commands (PlaceOrderCommand) and publishes events (OrderPlaced) and applies (replays) the events to reproduce the state:
```
@Data
public class Order extends ReflectiveMutableCommandProcessingAggregate<Order, OrderCommand> {

    
    private String id;
    
    private String productId;
    
    private Integer qty;
    
    private String customerId;
    
    private Double amount;
    
    private String status;
    
    private String address;


    public List<Event> process(PlaceOrderCommand cmd) {
        OrderPlaced orderPlaced = new OrderPlaced();
        BeanUtils.copyProperties(cmd, orderPlaced);
       // orderPlaced.setId(java.util.UUID.randomUUID().toString());

        return events(orderPlaced);
    }
    
    public void apply(OrderPlaced event) {
        BeanUtils.copyProperties(event, this);
    }
    

    public List<Event> process(CancelOrderCommand cmd) {
        OrderCancelled orderCancelled = new OrderCancelled();
        //orderCancelled.setId(cmd.getId());

        return events(orderCancelled);
    }

    public void apply(OrderCancelled event){
        setStatus("CANCELLED");
        
    }

}

```

- Aggregate "Inventory.java" patches commands (SetInventory, DescreaseStock, IncreaseStock) and publishes events (InventoryCreated, InventoryDecreased, InventoryIncreased) and applies (replays) the events to reproduce the state:
```
@Data
public class Inventory extends ReflectiveMutableCommandProcessingAggregate<Inventory, InventoryCommand> {

    private String id;
    
    private Long stock;

    public List<Event> process(SetInventoryCommand command){
        InventoryCreated inventoryCreated = new InventoryCreated();
        BeanUtils.copyProperties(command, inventoryCreated);
        return events(inventoryCreated);        
    }

    public void apply(InventoryCreated event){
        setStock(event.getStock()); 
    }

    public List<Event> process(DecreaseStockCommand command){
        InventoryDecreased event = new InventoryDecreased();
        BeanUtils.copyProperties(command, event);
        return events(event);
    }

    public void apply(InventoryDecreased stockDecreased){
        setStock(getStock() - stockDecreased.getQty()); 
    }

    public List<Event> process(IncreaseStockCommand command){
        InventoryIncreased event = new InventoryIncreased();
        BeanUtils.copyProperties(command, event);
        return events(event);
    }

    public void apply(InventoryIncreased event){
        setStock(getStock() + event.getQty()); 
    }


}
```


- As stated in "application.yaml", Eventuate Local uses the configuration to connect to the database and store events to the table "message" and the Eventuate CDC pick up the message from the db log and send events to the kafka:
```
spring:
  profiles: default
  jpa:
    generate-ddl: true
    properties:
      hibernate:
        show_sql: true
        format_sql: true

  datasource:
    url: jdbc:mysql://${DOCKER_HOST_IP:localhost}/eventuate
    username: mysqluser
    password: mysqlpw
    driver-class-name: com.mysql.cj.jdbc.Driver


eventuatelocal:
  kafka:
    bootstrap.servers: ${DOCKER_HOST_IP:localhost}:9092


cdc:
  service:
    url: http://localhost:8099

```

- Remember, Compare to eventuate tram, for eventuate CDC setting, you need to tell the CDC must run for eventuate local mode:

in kafka/docker-compose file:
```
    environment:
      ...
      EVENTUATE_CDC_TYPE: "EventuateLocal"
```

- In inventory service, the OrderPlaced events are subscribed by the EventSubscriber (in eventuate-tram, EventDispatcher is used in turn) :
```
@EventSubscriber(id = "inventoryPolicyHandler")
public class PolicyHandler{    
   
    @EventHandlerMethod
    public CompletableFuture<EntityWithIdAndVersion<Inventory>> wheneverOrderPlaced_DecreaseInventory(
            EventHandlerContext<OrderPlaced> ctx) {
                
      OrderPlaced event = ctx.getEvent();
      int qty = event.getQty();
      String productId = event.getProductId();
      String orderId = ctx.getEntityId();
  
      DecreaseStockCommand command = new DecreaseStockCommand();
      command.setQty(qty);

      return ctx.update(Inventory.class, productId, command);
    }
   
}
```


## References
- Eventuate Official Doc: https://eventuate.io/whyeventsourcing.html
- Orchestration version: https://github.com/jinyoung/lab-shop-eventuate-orchestration

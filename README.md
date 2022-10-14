# Microservice Choreography with Eventuate + Spring Data REST

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
http :8082/inventories id=1 stock=10
http :8081/orders productId=1 qty=5
```

check the stock remaining:
```
http :8082/inventories/1    #stock must be 5
```

- check the database:

```
docker exec -it kafka-mysql-1 /bin/bash

mysql --user=root --password=$MYSQL_ROOT_PASSWORD
use eventuate;
select * from message;
```

you can see:
```
+-----------------------------------+-------------------------------+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------+-----------+-------------------+---------------+
| id                                | destination                   | headers                                                                                                                                                                                                                                                                               | payload | published | message_partition | creation_time |
+-----------------------------------+-------------------------------+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------+-----------+-------------------+---------------+
| 00000183c7c93c05-f2d3907995f30000 | labshopeventuate.domain.Order | {"PARTITION_ID":"1","event-aggregate-type":"labshopeventuate.domain.Order","DATE":"Tue, 11 Oct 2022 16:03:17 GMT","event-aggregate-id":"1","event-type":"labshopeventuate.domain.OrderPlaced","DESTINATION":"labshopeventuate.domain.Order","ID":"00000183c7c93c05-f2d3907995f30000"} | {}      |         0 |              NULL | 1665504197650 |
+-----------------------------------+-------------------------------+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------+-----------+-------------------+---------------+
```

you can find the kafka log:

```
docker exec -it kafka-kafka-1 /bin/bash
cd /bin

kafka-topics --bootstrap-server=localhost:9092 --list
kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Order --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic labshopeventuate.domain.Order --from-beginning
kafka-consumer-groups --bootstrap-server localhost:9092 --group inventory --topic labshopeventuate.domain.Order --reset-offsets --to-last --execute
```

you can see:
```
{"payload":"{}","headers":{"PARTITION_ID":"1","event-aggregate-type":"labshopeventuate.domain.Order","DATE":"Tue, 11 Oct 2022 16:03:17 GMT","event-aggregate-id":"1","event-type":"labshopeventuate.domain.OrderPlaced","DESTINATION":"labshopeventuate.domain.Order","ID":"00000183c7c93c05-f2d3907995f30000"}}
```


## Possible Errors:

- org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'domainEventDispatcher' defined in labshopeventuate.InventoryApplication: Unsatisfied dependency expressed through method 'domainEventDispatcher' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'domainEventDispatcherFactory' defined in class path resource [io/eventuate/tram/spring/events/subscriber/TramEventSubscriberConfiguration.class]: Unsatisfied dependency expressed through method 'domainEventDispatcherFactory' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'messageConsumer' defined in class path resource [io/eventuate/tram/spring/consumer/common/TramConsumerCommonConfiguration.class]: Unsatisfied dependency expressed through method 'messageConsumer' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'messageConsumerImplementation' defined in class path resource [io/eventuate/tram/spring/consumer/kafka/EventuateTramKafkaMessageConsumerConfiguration.class]: Unsatisfied dependency expressed through method 'messageConsumerImplementation' parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'messageConsumerKafka' defined in class path reso

- when you missed to configure eventuate to point to the kafka in the application.yaml



내부에 뭔가 이상한 토픽에다가 mysql 과 동기화하는 작업에 대한 값을 관리한다:
```
./kafka-console-consumer --bootstrap-server localhost:9092 --topic offset.storage.topic --from-beginning

{"binlogFilename":"mysql-bin.000003","offset":152871,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153055,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153223,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153407,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153575,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153759,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":153927,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":154111,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":154279,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":154463,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":154631,"rowsToSkip":0}
{"binlogFilename":"mysql-bin.000003","offset":154815,"rowsToSkip":0}
```



## Implementation Details

- "Order::onPostPersist" publishes events with DomainEventPublisher service that is provided by Eventuate:
```
    @PostPersist  // will be invoked after saving Order aggreagte
    public void onPostPersist(){  
        OrderPlaced orderPlaced = new OrderPlaced(this);

        DomainEventPublisher publisher = OrderApplication.applicationContext.getBean(DomainEventPublisher.class);
        publisher.publish(getClass(), getId(), Collections.singletonList(orderPlaced));
    }

```

- As stated in "application.yaml", Eventuate Tram uses the configuration to connect to the database and store messages to the table "message" and the Eventuate CDC pick up the message from the db log and send events to the kafka:
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

- In inventory service, the events are subscribed by the EventDispatcher:
```
    # InventoryApplication.java

    @Bean
    public DomainEventDispatcher domainEventDispatcher(DomainEventDispatcherFactory domainEventDispatcherFactory) {
      return domainEventDispatcherFactory.make("orderServiceEvents", DomainEventHandlersBuilder
      .forAggregateType("labshopeventuate.domain.Order")
      .onEvent(OrderPlaced.class, PolicyHandler::wheneverOrderPlaced_DecreaseStock)
      //.onEvent(OrderCancelledEvent.class, this::handleOrderCancelledEvent)
      .build());
    }
```

- EventDispatcher calls PolicyHandler and the Policyhandler invokes the aggregate's port method (decreaseStock):
```
    public static void wheneverOrderPlaced_DecreaseStock(DomainEventEnvelope<OrderPlaced> orderPlacedEvent){

        OrderPlaced event = orderPlacedEvent.getEvent();
        System.out.println("\n\n##### listener DecreaseStock : " + event + "\n\n");

        if(event.getProductId()!=null) 
            Inventory.decreaseStock(event);
        
    }
```

## References
- Eventuate Official Doc: https://eventuate.io/abouteventuatetram.html
- Orchestration version: https://github.com/jinyoung/lab-shop-eventuate-orchestration

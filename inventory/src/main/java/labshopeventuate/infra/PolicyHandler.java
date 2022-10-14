package labshopeventuate.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import labshopeventuate.domain.*;



public class PolicyHandler{    
    
    // @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderPlaced'")


    public static void wheneverOrderPlaced_DecreaseStock(DomainEventEnvelope<OrderPlaced> orderPlacedEvent){

        OrderPlaced event = orderPlacedEvent.getEvent();
        System.out.println("\n\n##### listener DecreaseStock : " + event + "\n\n");

        if(event.getProductId()!=null) 
            Inventory.decreaseStock(event);
        
    }

    // @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCancelled'")
    // public void wheneverOrderCancelled_IncreaseStock(@Payload OrderCancelled orderCancelled){

    //     OrderCancelled event = orderCancelled;
    //     System.out.println("\n\n##### listener IncreaseStock : " + orderCancelled + "\n\n");


        

    //     // Sample Logic //
    //     Inventory.increaseStock(event);
        

        

    // }

}



package labshopeventuate.domain;

import labshopeventuate.domain.*;
import lombok.*;
import java.util.*;

import io.eventuate.tram.events.common.DomainEvent;
@Data
@ToString
public class OrderPlaced implements DomainEvent {

    private Long id;
    private String productId;
    private Integer qty;
    private String customerId;
    private Double amount;
    private String status;
    private String address;
}



package labshopeventuate.domain;

import labshopeventuate.domain.*;
import lombok.*;
import java.util.*;

import io.eventuate.Event;
import io.eventuate.EventEntity;

@Data
@ToString
@EventEntity(entity = "labshopeventuate.domain.Order")
public class OrderPlaced implements Event {

    private String productId;
    private int qty;
}



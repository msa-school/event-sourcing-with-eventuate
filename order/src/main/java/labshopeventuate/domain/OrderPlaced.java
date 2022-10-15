package labshopeventuate.domain;

import labshopeventuate.domain.*;
//import labshopeventuate.infra.AbstractEvent;
import java.util.*;

import org.springframework.beans.BeanUtils;

import io.eventuate.Event;
import io.eventuate.EventEntity;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EventEntity(entity = "labshopeventuate.domain.Order")
public class OrderPlaced implements Event{//extends AbstractEvent {
    int qty;
    String productId;
    
}

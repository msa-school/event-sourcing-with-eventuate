package labshopeventuate.domain;

import labshopeventuate.domain.*;
//import labshopeventuate.infra.AbstractEvent;
import java.util.*;

import org.springframework.beans.BeanUtils;

import io.eventuate.Event;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlaced implements Event{//extends AbstractEvent {
    int qty;
    String productId;
    
}

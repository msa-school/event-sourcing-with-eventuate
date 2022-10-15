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

    //private String id;
    private Integer qty;
    private String customerId;
    private Double productId;

}

package labshopeventuate.domain;

import labshopeventuate.domain.*;
import lombok.*;
import java.util.*;

import io.eventuate.Event;

@Data
@ToString
public class OrderCancelled implements Event {

    private Long id;
    private String productId;
    private Integer qty;
}



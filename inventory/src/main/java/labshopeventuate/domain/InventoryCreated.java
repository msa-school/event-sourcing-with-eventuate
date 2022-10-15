package labshopeventuate.domain;

import labshopeventuate.domain.*;
import lombok.*;
import java.util.*;

import io.eventuate.Event;

@Data
@ToString
public class InventoryCreated implements Event {

    private Long stock;
}



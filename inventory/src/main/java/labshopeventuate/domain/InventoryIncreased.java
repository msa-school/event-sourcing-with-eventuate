package labshopeventuate.domain;

import io.eventuate.Event;
import lombok.Data;

@Data
public class InventoryIncreased implements Event{
    int qty;
}

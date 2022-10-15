package labshopeventuate.domain;

import io.eventuate.Event;
import lombok.Data;

@Data
public class InventoryDecreased implements Event{
    int qty;
}

package labshopeventuate.domain;

import lombok.Data;

@Data
public class SetInventoryCommand implements InventoryCommand{

    String productId;
    Long stock;

}

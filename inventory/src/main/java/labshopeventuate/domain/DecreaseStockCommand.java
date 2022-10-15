package labshopeventuate.domain;

import lombok.Data;

@Data
public class DecreaseStockCommand implements InventoryCommand{

    int qty;
    
}

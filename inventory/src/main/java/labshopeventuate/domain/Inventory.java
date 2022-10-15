package labshopeventuate.domain;

import io.eventuate.Event;
import io.eventuate.ReflectiveMutableCommandProcessingAggregate;

import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;

import static io.eventuate.EventUtil.events;


@Data
public class Inventory extends ReflectiveMutableCommandProcessingAggregate<Inventory, InventoryCommand> {

    private String id;
    
    private Long stock;

    public List<Event> process(SetInventoryCommand command){
        InventoryCreated inventoryCreated = new InventoryCreated();
        BeanUtils.copyProperties(command, inventoryCreated);
        return events(inventoryCreated);        
    }

    public void apply(InventoryCreated event){
        setStock(event.getStock()); 
    }

    public List<Event> process(DecreaseStockCommand command){
        InventoryDecreased event = new InventoryDecreased();
        BeanUtils.copyProperties(command, event);
        return events(event);
    }

    public void apply(InventoryDecreased stockDecreased){
        setStock(getStock() - stockDecreased.getQty()); 
    }

    public List<Event> process(IncreaseStockCommand command){
        InventoryIncreased event = new InventoryIncreased();
        BeanUtils.copyProperties(command, event);
        return events(event);
    }

    public void apply(InventoryIncreased event){
        setStock(getStock() + event.getQty()); 
    }


}

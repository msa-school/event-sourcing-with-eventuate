package labshopeventuate.infra;
import labshopeventuate.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.EntityWithMetadata;
import io.eventuate.SaveOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
@Transactional
public class InventoryController {
    @Autowired
    AggregateRepository<Inventory, InventoryCommand> inventoryRepository;


    @RequestMapping(path="/inventories", method=RequestMethod.POST)
    public Inventory createInventory(@RequestBody SetInventoryCommand setInventoryCommand){
        return aggregateWithId(
            inventoryRepository.save(
                setInventoryCommand, 
                Optional.of(new SaveOptions().withId(setInventoryCommand.getProductId()))
            )
        );
    }

    @RequestMapping(path="/inventories/{id}", method=RequestMethod.GET)
    public Inventory getInventory(@PathVariable("id") String productId){
        EntityWithMetadata<Inventory> result = 
            inventoryRepository.find(
                productId
            );
        
        Inventory inventory = result.getEntity();
        inventory.setId(productId);

        return inventory;
        
    }

    public Inventory aggregateWithId(EntityWithIdAndVersion<Inventory> result){
        Inventory inventory = result.getAggregate();
        inventory.setId(result.getEntityId());

        return inventory;
    }


}

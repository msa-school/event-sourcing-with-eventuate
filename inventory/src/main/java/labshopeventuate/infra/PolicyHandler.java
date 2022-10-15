package labshopeventuate.infra;

import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.EventHandlerContext;
import io.eventuate.EventHandlerMethod;
import io.eventuate.EventSubscriber;
import labshopeventuate.domain.DecreaseStockCommand;
import labshopeventuate.domain.Inventory;
import labshopeventuate.domain.OrderPlaced;

import java.util.concurrent.CompletableFuture;

@EventSubscriber(id = "inventoryPolicyHandler")
public class PolicyHandler{    
   
    @EventHandlerMethod
    public CompletableFuture<EntityWithIdAndVersion<Inventory>> wheneverOrderPlaced_DecreaseInventory(
            EventHandlerContext<OrderPlaced> ctx) {
                
      OrderPlaced event = ctx.getEvent();
      int qty = event.getQty();
      String productId = event.getProductId();
      String orderId = ctx.getEntityId();
  
      DecreaseStockCommand command = new DecreaseStockCommand();
      command.setQty(qty);

      return ctx.update(Inventory.class, productId, command);
    }
  
   
}



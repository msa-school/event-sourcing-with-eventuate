package labshopeventuate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.sync.EventuateAggregateStore;
import labshopeventuate.domain.Inventory;
import labshopeventuate.domain.InventoryCommand;
import labshopeventuate.domain.OrderPlaced;
import labshopeventuate.infra.PolicyHandler;

import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class InventoryApplication {
    public static ApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    public AggregateRepository<Inventory, InventoryCommand> orderRepository(EventuateAggregateStore eventStore) {
      return new AggregateRepository<>(Inventory.class, eventStore);
    }
  
}
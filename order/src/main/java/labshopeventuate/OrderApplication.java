package labshopeventuate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.sync.EventuateAggregateStore;
import labshopeventuate.domain.Order;
import labshopeventuate.domain.OrderCommand;

import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class OrderApplication {
    public static ApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public AggregateRepository<Order, OrderCommand> orderRepository(EventuateAggregateStore eventStore) {
      return new AggregateRepository<>(Order.class, eventStore);
    }
}
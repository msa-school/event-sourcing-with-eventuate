package labshopeventuate.domain;

import lombok.Data;

@Data
public class CancelOrderCommand implements OrderCommand{
    String id;
}

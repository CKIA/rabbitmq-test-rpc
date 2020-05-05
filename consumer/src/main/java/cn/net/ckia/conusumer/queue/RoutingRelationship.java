package cn.net.ckia.conusumer.queue;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Builder
@ToString
public class RoutingRelationship implements Serializable {

    private String routingKey;
    private String exchangeName;
    private String exchangeTypes;

}

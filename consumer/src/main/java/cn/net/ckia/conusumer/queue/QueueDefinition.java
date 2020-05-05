package cn.net.ckia.conusumer.queue;

import org.springframework.amqp.core.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueueDefinition extends Queue {
    private List<RoutingRelationship> routingRelationships ;

    public QueueDefinition(String name) {
        super(name);
    }

    public QueueDefinition(String name, boolean durable) {
        super(name, durable);
    }

    public QueueDefinition(String name, boolean durable, boolean exclusive, boolean autoDelete) {
        super(name, durable, exclusive, autoDelete);
    }

    public QueueDefinition(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, exclusive, autoDelete, arguments);
    }

    public List<RoutingRelationship> getRoutingRelationships() {
        return routingRelationships;
    }

    public void addRoutingRelationships(RoutingRelationship routingRelationship) {
        if (null == this.routingRelationships) {
            this.routingRelationships = new ArrayList<>(16);
        }
        routingRelationships.add(routingRelationship);
    }
}

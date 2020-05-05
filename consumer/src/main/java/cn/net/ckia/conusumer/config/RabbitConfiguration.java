package cn.net.ckia.conusumer.config;

import cn.net.ckia.conusumer.exanchge.DefaultExchange;
import cn.net.ckia.conusumer.queue.QueueConfig;
import cn.net.ckia.conusumer.queue.QueueDefinition;
import cn.net.ckia.conusumer.queue.QueueRoutingKeys;
import cn.net.ckia.conusumer.queue.RoutingRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Configuration
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() throws Exception{
        Properties properties = new Properties();
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        properties.load(resource);
        resource.close();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(properties.get("spring.rabbitmq.host").toString(),
                Integer.valueOf(properties.get("spring.rabbitmq.port").toString()));
        connectionFactory.setUsername(properties.get("spring.rabbitmq.username").toString());
        connectionFactory.setPassword(properties.get("spring.rabbitmq.password").toString());
        connectionFactory.setVirtualHost(properties.get("spring.rabbitmq.virtual-host").toString());
        return connectionFactory;
    }

    @Bean(DefaultExchange.DIRECT_EXCHANGE)
    public DirectExchange directExchange() {
        return new DirectExchange(DefaultExchange.DIRECT_EXCHANGE,true,false);
    }

    /** 声明队列信息 */
    @Bean(QueueConfig.FIRST_QUEUE)
    public Queue firstQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.FIRST_QUEUE);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DIRECT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueRoutingKeys.FIRST_QUEUE)
                .build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }
    /** 声明队列信息 */
    @Bean(QueueConfig.SECOND_QUEUE)
    public Queue secondQueue () {
        QueueDefinition queue = new QueueDefinition(QueueConfig.SECOND_QUEUE);
        RoutingRelationship relationship = RoutingRelationship.builder()
                .exchangeName(DefaultExchange.DIRECT_EXCHANGE)
                .exchangeTypes(ExchangeTypes.DIRECT)
                .routingKey(QueueRoutingKeys.SECOND_QUEUE)
                .build();
        queue.addRoutingRelationships(relationship);
        return queue;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        //必须是prototype类型
        //Reply received after timeout
        RabbitTemplate rabbitTemplate =  new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReceiveTimeout(9000);
        return rabbitTemplate;
    }
}

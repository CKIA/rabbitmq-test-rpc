package cn.net.ckia.producter.config;

import cn.net.ckia.producter.queue.QueueConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {


    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory,
//                                         @Qualifier("standardSender") RabbitTemplate.ConfirmCallback standardSender,
//                                         @Qualifier("returnCallbackImpl") RabbitTemplate.ReturnCallback returnCallbackImpl) {

//        public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        template.setReturnCallback(returnCallbackImpl);
//        template.setConfirmCallback(standardSender);
//        template.containerAckMode(AcknowledgeMode.MANUAL);
//        template.setMandatory(true);
//        return template;
//    }
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        //设置reply_to（返回队列，只能在这设置）
//        rabbitTemplate.setReplyAddress(QueueConfig.SECOND_QUEUE);
//        rabbitTemplate.setReplyTimeout(60000);
//        return rabbitTemplate;
//    }
//
//    //返回队列监听器（必须有）
//    @Bean(name="replyMessageListenerContainer")
//    public SimpleMessageListenerContainer createReplyListenerContainer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
//        listenerContainer.setConnectionFactory(connectionFactory);
//        listenerContainer.setQueueNames(QueueConfig.SECOND_QUEUE);
//        listenerContainer.setMessageListener(rabbitTemplate(connectionFactory));
//        return listenerContainer;
//    }
}

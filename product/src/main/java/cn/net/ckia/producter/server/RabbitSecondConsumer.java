package cn.net.ckia.producter.server;

import cn.net.ckia.producter.pojo.MessageResponse;
import cn.net.ckia.producter.queue.QueueConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = QueueConfig.SECOND_QUEUE)
public class RabbitSecondConsumer extends RabbitmqConsumer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public RabbitSecondConsumer() {
        super(0, 0);
    }

//    @RabbitHandler
//    @RabbitListener(queues = QueueConfig.SECOND_QUEUE)
    public void receive(Message message, Channel channel) {
        super.processHandler(message,channel);
    }
    @RabbitHandler
    public void receiveAsync(@Payload String msg, @Headers MessageHeaders headers,Channel channel,Message message){
        super.processHandler(message,channel);
        log.info("异步rpc调用消息进入");
    }

    @Override
    public void businessProcess(Message message) {
        log.info(message.getMessageProperties().getReceivedExchange()+"暂时无业务处理");
    }

    @Override
    public void messageProcess(Channel channel,Message message) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        String replyTo = messageProperties.getReplyTo();
        MessageResponse messageResponse = MessageResponse.builder().message("我收到消息了:" + new String(message.getBody())).build();
        rabbitTemplate.convertAndSend(replyTo,messageResponse.toString(), retMessage ->{
            MessageProperties properties = retMessage.getMessageProperties();
            properties.setCorrelationId( messageProperties.getCorrelationId());
            properties.setContentEncoding(messageProperties.getContentEncoding());
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            return retMessage;
        });
    }

    @Override
    public void messageErrorProcess(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

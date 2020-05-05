package cn.net.ckia.producter.publish;

import cn.net.ckia.producter.exanchge.DefaultExchange;
import cn.net.ckia.producter.queue.QueueRoutingKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.postprocessor.DeflaterPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Component
public class StandardSender implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    public void sendRpcMsg(String msg) {
        CorrelationData data = new CorrelationData(UUID.randomUUID().toString());
        MessageProperties messageProperties = new MessageProperties();
        byte[] msgBytes = msg.getBytes();
        messageProperties.setContentLength(msgBytes.length);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setReplyTo(QueueRoutingKeys.SECOND_QUEUE);
        Message message = new Message(msgBytes,messageProperties);
        DeflaterPostProcessor postProcessor = new DeflaterPostProcessor();
        Message message1 = postProcessor.postProcessMessage(message, data);
        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setReplyAddress(QueueRoutingKeys.SECOND_QUEUE);
        Object receive = rabbitTemplate.convertSendAndReceive(DefaultExchange.DIRECT_EXCHANGE, QueueRoutingKeys.FIRST_QUEUE, msg, null, data);
        log.info("receive:",receive);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功");
            return;
        }
        log.error("消息发送失败,case:{},data:{}",cause,null== correlationData ?correlationData:correlationData.toString());

    }
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息被退回,message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",message,replyCode,replyText,exchange,routingKey);
    }

}

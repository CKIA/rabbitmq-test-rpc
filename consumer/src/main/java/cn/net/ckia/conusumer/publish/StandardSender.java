package cn.net.ckia.conusumer.publish;

import cn.net.ckia.conusumer.exanchge.DefaultExchange;
import cn.net.ckia.conusumer.queue.QueueRoutingKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Component
public class StandardSender implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    @Qualifier("connectionFactory")
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    public Object sendRpcMsg(String msg) {
        CorrelationData data = new CorrelationData(UUID.randomUUID().toString());
        return rabbitTemplate.convertSendAndReceive(DefaultExchange.DIRECT_EXCHANGE, QueueRoutingKeys.FIRST_QUEUE, msg, data);
    }
    public void sendAsyncRpcMsg(String msg) {
        AsyncRabbitTemplate asyncRabbitTemplate =  new AsyncRabbitTemplate(connectionFactory,DefaultExchange.DIRECT_EXCHANGE, QueueRoutingKeys.SECOND_QUEUE);
        asyncRabbitTemplate.setReceiveTimeout(9000);
        asyncRabbitTemplate.start();
        AsyncRabbitTemplate.RabbitConverterFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive(DefaultExchange.DIRECT_EXCHANGE, QueueRoutingKeys.SECOND_QUEUE, msg);
        future.addCallback(new ListenableFutureCallback<Object>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("rpc调用失败:{}",throwable.getStackTrace());
            }
            @Override
            public void onSuccess(Object o) {
                log.info("success :{}", new String((byte[]) o));
            }
        });
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

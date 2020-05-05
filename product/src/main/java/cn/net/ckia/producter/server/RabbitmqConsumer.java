package cn.net.ckia.producter.server;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class RabbitmqConsumer {

    /**
     * 重试次数
     */
    protected final Integer retryFrequency;
    /**
     * 重试间隔
     */
    protected final Integer interval;

    private ThreadLocal<AtomicInteger>  localRetryFrequency = new ThreadLocal<AtomicInteger>();

    protected RabbitmqConsumer(Integer retryFrequency, Integer interval) {
        this.retryFrequency = retryFrequency;
        this.interval = interval;
    }

    public abstract void receive(Message message, Channel channel);

    protected void processHandler(Message message, Channel channel) {
        if(null ==localRetryFrequency.get()) localRetryFrequency.set(new AtomicInteger(0));

        MessageProperties messageProperties = message.getMessageProperties();
        String exchange = messageProperties.getReceivedExchange();
        String routingKey = messageProperties.getReceivedRoutingKey();
        try {
            if (log.isDebugEnabled()) {
                log.debug(exchange +","+ routingKey +",message:{},deliveryTag:{}",new String(message.getBody(),messageProperties.getContentEncoding()),messageProperties.getDeliveryTag());
            }
            if (log.isInfoEnabled()) {
                log.info(exchange +","+ routingKey +",message:{},deliveryTag:{}",new String(message.getBody(),messageProperties.getContentEncoding()),messageProperties.getDeliveryTag());
            }
            businessProcess(message);
            messageProcess(channel,message);
        } catch (Exception e) {
            log.error(exchange +","+ routingKey +"error:{}", Arrays.asList(e.getStackTrace()));
            if (localRetryFrequency.get().get() != this.retryFrequency) {
                retry(message,channel);
            } else {
                log.error("消息重试{}次依然失败,message:{}",this.retryFrequency,message);
                messageErrorProcess(message,channel);
            }
        }
    }

    protected void retry(Message message, Channel channel) {
        AtomicInteger retryStart = localRetryFrequency.get();
        log.info("正在重试发送消息,第{}次,message:{}",retryStart.get()+1,message);
        localRetryFrequency.get().incrementAndGet();
        if (0 <= this.interval) {
            try {
                Thread.currentThread().sleep(this.interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("消息重试间隔异常,error:{}",e.getStackTrace());
            }
        }
        this.processHandler(message,channel);
    }

    /**
     * 业务处理方法
     * @param message
     * @throws Exception
     */
    public abstract void businessProcess(Message message) throws Exception;

    /**
     * 消息处理,正常处理完成时调用
     * @param channel
     * @param message
     * @throws Exception
     */
    public abstract void messageProcess(Channel channel,Message message) throws Exception;

    /**
     * 消息处理异常时调用
     * @param channel
     * @param channel
     */
    public abstract void messageErrorProcess(Message message, Channel channel);
}

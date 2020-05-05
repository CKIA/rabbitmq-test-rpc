package cn.net.ckia.conusumer.consumer;

public interface TopicConsumer {
    /**
     * 消费的队列
     *
     * @return 队列
     */
    String getQueueEnum();

    /**
     * 具体消费者的实现
     *
     * @param message 消息
     */
    void handleMessage(String message);
}
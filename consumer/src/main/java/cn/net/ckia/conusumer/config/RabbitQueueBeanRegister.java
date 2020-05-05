package cn.net.ckia.conusumer.config;

import cn.net.ckia.conusumer.queue.QueueDefinition;
import cn.net.ckia.conusumer.queue.RoutingRelationship;
import org.springframework.amqp.core.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author onlinever
 * @date 2018/09/06
 */
@Service
public class RabbitQueueBeanRegister implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        //声明队列和绑定
        declareQueueAndBinding(applicationContext, beanDefinitionRegistry);
    }

    /**
     * 声明队列和绑定
     * @param applicationContext
     * @param beanDefinitionRegistry
     */
    private static void declareQueueAndBinding(ApplicationContext applicationContext, BeanDefinitionRegistry beanDefinitionRegistry) {
        final String bindingSuffix = "Binding";
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Queue.class);
        for (String queueName : beanNamesForType) {
            QueueDefinition queue = (QueueDefinition) applicationContext.getBean(queueName);
            List<RoutingRelationship> relationships = queue.getRoutingRelationships();
            for (RoutingRelationship routing : relationships) {
                switch (routing.getExchangeTypes()) {
                    case ExchangeTypes.DIRECT:
                        beanDefinitionRegistry.registerBeanDefinition(queueName + DirectExchange.class.getSimpleName() + bindingSuffix,
                                BeanDefinitionBuilder.genericBeanDefinition(
                                        Binding.class, () ->
                                                BindingBuilder.bind(queue)
                                                        .to(applicationContext.getBean(routing.getExchangeName(), DirectExchange.class))
                                                        .with(routing.getRoutingKey())
                                ).getBeanDefinition());
                        break;
                    case "custom":
                        beanDefinitionRegistry.registerBeanDefinition(queueName + DirectExchange.class.getSimpleName() + bindingSuffix,
                                BeanDefinitionBuilder.genericBeanDefinition(
                                        Binding.class,() ->
                                                BindingBuilder.bind(queue)
                                                        .to(applicationContext.getBean(routing.getExchangeName(), CustomExchange.class))
                                                        .with(routing.getRoutingKey())
                                                        .noargs()
                                ).getBeanDefinition());
                        break;
                    case ExchangeTypes.TOPIC:
                        beanDefinitionRegistry.registerBeanDefinition(queueName + TopicExchange.class.getSimpleName() + bindingSuffix,
                                BeanDefinitionBuilder.genericBeanDefinition(
                                        Binding.class, () ->
                                                BindingBuilder.bind(queue)
                                                        .to(applicationContext.getBean(routing.getExchangeName(), TopicExchange.class))
                                                        .with(routing.getRoutingKey())
                                ).getBeanDefinition());
                        break;
                    case ExchangeTypes.FANOUT:
                        beanDefinitionRegistry.registerBeanDefinition(queueName + FanoutExchange.class.getSimpleName() + bindingSuffix,
                                BeanDefinitionBuilder.genericBeanDefinition(
                                        Binding.class, () ->
                                                BindingBuilder.bind(queue)
                                                        .to(applicationContext.getBean(routing.getExchangeName(), FanoutExchange.class))
                                ).getBeanDefinition());
                        break;
                    case ExchangeTypes.HEADERS:
//                        beanDefinitionRegistry.registerBeanDefinition(queueName + bindingSuffix, BeanDefinitionBuilder.genericBeanDefinition(Binding.class, () ->
//                                BindingBuilder.bind(queue)
//                                        .to(applicationContext.getBean(routing.getExchangeName(), HeadersExchange.class))).getBeanDefinition());
                        break;
//                    case :
//                        break;
                    default:
                        break;
                }

            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

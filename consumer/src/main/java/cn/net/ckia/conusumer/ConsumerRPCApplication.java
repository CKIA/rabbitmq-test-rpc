package cn.net.ckia.conusumer;

import org.springframework.amqp.core.Binding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class ConsumerRPCApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConsumerRPCApplication.class, args);
        String[] beanNamesForType = applicationContext.getBeanNamesForType(Binding.class);
        Arrays.asList(beanNamesForType).forEach(e -> {
            Object bean = applicationContext.getBean(e);
            System.out.println(bean);
        });
    }

}

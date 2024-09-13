package com.compute_process.middleware.configuraton;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri("amqps://qypgxsvh:eCL_9EIo1lbiu-0hUXf61g-ZTavOL_WM@puffin.rmq2.cloudamqp.com/qypgxsvh");
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue("taskQueue",false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("taskExchange");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routingKey");
    }
}


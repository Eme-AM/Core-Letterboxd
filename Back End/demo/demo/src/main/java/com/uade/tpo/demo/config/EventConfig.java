package com.uade.tpo.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRabbit
@EnableRetry
public class EventConfig {
    
    // Exchanges
    public static final String LETTERBOXD_EXCHANGE = "letterboxd.events";
    public static final String DLX_EXCHANGE = "letterboxd.dlx";
    
    // Queues
    public static final String INCOMING_EVENTS_QUEUE = "letterboxd.incoming.events";
    public static final String USER_EVENTS_QUEUE = "letterboxd.user.events";
    public static final String MOVIE_EVENTS_QUEUE = "letterboxd.movie.events";
    public static final String REVIEW_EVENTS_QUEUE = "letterboxd.review.events";
    public static final String SOCIAL_EVENTS_QUEUE = "letterboxd.social.events";
    public static final String DISCOVERY_EVENTS_QUEUE = "letterboxd.discovery.events";
    public static final String ANALYTICS_EVENTS_QUEUE = "letterboxd.analytics.events";
    public static final String RETRY_QUEUE = "letterboxd.retry";
    public static final String DLQ_QUEUE = "letterboxd.dlq";
    
    // Routing Keys
    public static final String USER_ROUTING_KEY = "user.*";
    public static final String MOVIE_ROUTING_KEY = "movie.*";
    public static final String REVIEW_ROUTING_KEY = "review.*";
    public static final String SOCIAL_ROUTING_KEY = "social.*";
    public static final String DISCOVERY_ROUTING_KEY = "discovery.*";
    public static final String ANALYTICS_ROUTING_KEY = "analytics.*";
    
    @Bean
    public TopicExchange letterboxdExchange() {
        return ExchangeBuilder.topicExchange(LETTERBOXD_EXCHANGE)
                .durable(true)
                .build();
    }
    
    @Bean
    public DirectExchange dlxExchange() {
        return ExchangeBuilder.directExchange(DLX_EXCHANGE)
                .durable(true)
                .build();
    }
    
    // Incoming events queue - receives all events from modules
    @Bean
    public Queue incomingEventsQueue() {
        return QueueBuilder.durable(INCOMING_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    // Module-specific queues for routing
    @Bean
    public Queue userEventsQueue() {
        return QueueBuilder.durable(USER_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue movieEventsQueue() {
        return QueueBuilder.durable(MOVIE_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue reviewEventsQueue() {
        return QueueBuilder.durable(REVIEW_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue socialEventsQueue() {
        return QueueBuilder.durable(SOCIAL_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue discoveryEventsQueue() {
        return QueueBuilder.durable(DISCOVERY_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue analyticsEventsQueue() {
        return QueueBuilder.durable(ANALYTICS_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .withArgument("x-message-ttl", 30000) // 30 seconds TTL
                .withArgument("x-dead-letter-exchange", LETTERBOXD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "retry.process")
                .build();
    }
    
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    
    // Bindings
    @Bean
    public Binding incomingEventsBinding() {
        return BindingBuilder
                .bind(incomingEventsQueue())
                .to(letterboxdExchange())
                .with("*.*"); // Catch all events
    }
    
    @Bean
    public Binding userEventsBinding() {
        return BindingBuilder
                .bind(userEventsQueue())
                .to(letterboxdExchange())
                .with(USER_ROUTING_KEY);
    }
    
    @Bean
    public Binding movieEventsBinding() {
        return BindingBuilder
                .bind(movieEventsQueue())
                .to(letterboxdExchange())
                .with(MOVIE_ROUTING_KEY);
    }
    
    @Bean
    public Binding reviewEventsBinding() {
        return BindingBuilder
                .bind(reviewEventsQueue())
                .to(letterboxdExchange())
                .with(REVIEW_ROUTING_KEY);
    }
    
    @Bean
    public Binding socialEventsBinding() {
        return BindingBuilder
                .bind(socialEventsQueue())
                .to(letterboxdExchange())
                .with(SOCIAL_ROUTING_KEY);
    }
    
    @Bean
    public Binding discoveryEventsBinding() {
        return BindingBuilder
                .bind(discoveryEventsQueue())
                .to(letterboxdExchange())
                .with(DISCOVERY_ROUTING_KEY);
    }
    
    @Bean
    public Binding analyticsEventsBinding() {
        return BindingBuilder
                .bind(analyticsEventsQueue())
                .to(letterboxdExchange())
                .with(ANALYTICS_ROUTING_KEY);
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(dlqQueue())
                .to(dlxExchange())
                .with("failed");
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setDefaultRequeueRejected(false); // Send failed messages to DLQ
        return factory;
    }
}

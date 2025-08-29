package com.uade.tpo.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for Letterboxd Core Event Hub
 * Configures exchanges, queues, bindings, and message handling
 */
@Configuration
@EnableRabbit
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RabbitMQConfig {

    // Exchange Names
    @Value("${letterboxd.event-hub.routing.default-exchange:letterboxd.events}")
    private String defaultExchange;

    @Value("${letterboxd.event-hub.routing.dead-letter-exchange:letterboxd.dlx}")
    private String deadLetterExchange;

    // Queue Names
    @Value("${letterboxd.event-hub.queues.movies:movies.events}")
    private String moviesQueue;

    @Value("${letterboxd.event-hub.queues.users:users.events}")
    private String usersQueue;

    @Value("${letterboxd.event-hub.queues.reviews:reviews.events}")
    private String reviewsQueue;

    @Value("${letterboxd.event-hub.queues.social:social.events}")
    private String socialQueue;

    @Value("${letterboxd.event-hub.queues.discovery:discovery.events}")
    private String discoveryQueue;

    @Value("${letterboxd.event-hub.queues.analytics:analytics.events}")
    private String analyticsQueue;

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate Configuration
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message sent successfully: {}", correlationData);
            } else {
                log.error("Failed to send message: {} - Cause: {}", correlationData, cause);
            }
        });
        template.setReturnsCallback(returned -> {
            log.error("Message returned: {} - Reply Code: {} - Reply Text: {} - Exchange: {} - Routing Key: {}",
                    returned.getMessage(), returned.getReplyCode(), returned.getReplyText(),
                    returned.getExchange(), returned.getRoutingKey());
        });
        return template;
    }

    // Listener Container Factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    // === EXCHANGES ===

    @Bean
    public TopicExchange letterboxdEventsExchange() {
        return ExchangeBuilder
                .topicExchange(defaultExchange)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder
                .topicExchange(deadLetterExchange)
                .durable(true)
                .build();
    }

    // === QUEUES ===

    @Bean
    public Queue moviesEventsQueue() {
        return QueueBuilder
                .durable(moviesQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "movies.failed")
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    @Bean
    public Queue usersEventsQueue() {
        return QueueBuilder
                .durable(usersQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "users.failed")
                .withArgument("x-message-ttl", 3600000)
                .build();
    }

    @Bean
    public Queue reviewsEventsQueue() {
        return QueueBuilder
                .durable(reviewsQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "reviews.failed")
                .withArgument("x-message-ttl", 3600000)
                .build();
    }

    @Bean
    public Queue socialEventsQueue() {
        return QueueBuilder
                .durable(socialQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "social.failed")
                .withArgument("x-message-ttl", 3600000)
                .build();
    }

    @Bean
    public Queue discoveryEventsQueue() {
        return QueueBuilder
                .durable(discoveryQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "discovery.failed")
                .withArgument("x-message-ttl", 3600000)
                .build();
    }

    @Bean
    public Queue analyticsEventsQueue() {
        return QueueBuilder
                .durable(analyticsQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .withArgument("x-dead-letter-routing-key", "analytics.failed")
                .withArgument("x-message-ttl", 3600000)
                .build();
    }

    // Dead Letter Queues
    @Bean
    public Queue moviesDeadLetterQueue() {
        return QueueBuilder.durable("movies.failed").build();
    }

    @Bean
    public Queue usersDeadLetterQueue() {
        return QueueBuilder.durable("users.failed").build();
    }

    @Bean
    public Queue reviewsDeadLetterQueue() {
        return QueueBuilder.durable("reviews.failed").build();
    }

    @Bean
    public Queue socialDeadLetterQueue() {
        return QueueBuilder.durable("social.failed").build();
    }

    @Bean
    public Queue discoveryDeadLetterQueue() {
        return QueueBuilder.durable("discovery.failed").build();
    }

    @Bean
    public Queue analyticsDeadLetterQueue() {
        return QueueBuilder.durable("analytics.failed").build();
    }

    // === BINDINGS ===

    @Bean
    public Binding moviesBinding() {
        return BindingBuilder
                .bind(moviesEventsQueue())
                .to(letterboxdEventsExchange())
                .with("movies.*");
    }

    @Bean
    public Binding usersBinding() {
        return BindingBuilder
                .bind(usersEventsQueue())
                .to(letterboxdEventsExchange())
                .with("users.*");
    }

    @Bean
    public Binding reviewsBinding() {
        return BindingBuilder
                .bind(reviewsEventsQueue())
                .to(letterboxdEventsExchange())
                .with("reviews.*");
    }

    @Bean
    public Binding socialBinding() {
        return BindingBuilder
                .bind(socialEventsQueue())
                .to(letterboxdEventsExchange())
                .with("social.*");
    }

    @Bean
    public Binding discoveryBinding() {
        return BindingBuilder
                .bind(discoveryEventsQueue())
                .to(letterboxdEventsExchange())
                .with("discovery.*");
    }

    @Bean
    public Binding analyticsBinding() {
        return BindingBuilder
                .bind(analyticsEventsQueue())
                .to(letterboxdEventsExchange())
                .with("analytics.*");
    }

    // Dead Letter Bindings
    @Bean
    public Binding moviesDeadLetterBinding() {
        return BindingBuilder
                .bind(moviesDeadLetterQueue())
                .to(deadLetterExchange())
                .with("movies.failed");
    }

    @Bean
    public Binding usersDeadLetterBinding() {
        return BindingBuilder
                .bind(usersDeadLetterQueue())
                .to(deadLetterExchange())
                .with("users.failed");
    }

    @Bean
    public Binding reviewsDeadLetterBinding() {
        return BindingBuilder
                .bind(reviewsDeadLetterQueue())
                .to(deadLetterExchange())
                .with("reviews.failed");
    }

    @Bean
    public Binding socialDeadLetterBinding() {
        return BindingBuilder
                .bind(socialDeadLetterQueue())
                .to(deadLetterExchange())
                .with("social.failed");
    }

    @Bean
    public Binding discoveryDeadLetterBinding() {
        return BindingBuilder
                .bind(discoveryDeadLetterQueue())
                .to(deadLetterExchange())
                .with("discovery.failed");
    }

    @Bean
    public Binding analyticsDeadLetterBinding() {
        return BindingBuilder
                .bind(analyticsDeadLetterQueue())
                .to(deadLetterExchange())
                .with("analytics.failed");
    }
}

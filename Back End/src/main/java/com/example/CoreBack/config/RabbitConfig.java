package com.example.CoreBack.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchange principal
    public static final String EXCHANGE = "letterboxd_exchange";

    // Colas principales
    public static final String CORE_ALL_QUEUE = "core.all.queue";
    public static final String CORE_USERS_QUEUE = "core.users.queue";
    public static final String CORE_MOVIES_QUEUE = "core.movies.queue";
    public static final String CORE_RATINGS_QUEUE = "core.ratings.queue";
    public static final String CORE_SOCIAL_QUEUE = "core.social.queue";
    public static final String CORE_ANALYTICS_QUEUE = "core.analytics.queue";
    public static final String CORE_RECOMMENDATIONS_QUEUE = "core.recommendations.queue";

    // Routing keys personalizadas (ya no usamos los wildcard tipo *.created)
    // porque el Core decide a qué cola reenviar cada evento
    public static final String ROUTING_KEY_ALL = "core.all";
    public static final String ROUTING_KEY_USERS = "core.users";
    public static final String ROUTING_KEY_MOVIES = "core.movies";
    public static final String ROUTING_KEY_RATINGS = "core.ratings";
    public static final String ROUTING_KEY_SOCIAL = "core.social";
    public static final String ROUTING_KEY_ANALYTICS = "core.analytics";
    public static final String ROUTING_KEY_RECOMMENDATIONS = "core.recommendations";

    // Exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // Declaración de colas
    @Bean public Queue coreAllQueue() { return new Queue(CORE_ALL_QUEUE, true); }
    @Bean public Queue coreUsersQueue() { return new Queue(CORE_USERS_QUEUE, true); }
    @Bean public Queue coreMoviesQueue() { return new Queue(CORE_MOVIES_QUEUE, true); }
    @Bean public Queue coreRatingsQueue() { return new Queue(CORE_RATINGS_QUEUE, true); }
    @Bean public Queue coreSocialQueue() { return new Queue(CORE_SOCIAL_QUEUE, true); }
    @Bean public Queue coreAnalyticsQueue() { return new Queue(CORE_ANALYTICS_QUEUE, true); }
    @Bean public Queue coreRecommendationsQueue() { return new Queue(CORE_RECOMMENDATIONS_QUEUE, true); }

    // Bindings (uno a uno, sin wildcard)
    @Bean
    public Binding bindingAll(Queue coreAllQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAllQueue).to(exchange).with(ROUTING_KEY_ALL);
    }

    @Bean
    public Binding bindingUsers(Queue coreUsersQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreUsersQueue).to(exchange).with(ROUTING_KEY_USERS);
    }

    @Bean
    public Binding bindingMovies(Queue coreMoviesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreMoviesQueue).to(exchange).with(ROUTING_KEY_MOVIES);
    }

    @Bean
    public Binding bindingRatings(Queue coreRatingsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRatingsQueue).to(exchange).with(ROUTING_KEY_RATINGS);
    }

    @Bean
    public Binding bindingSocial(Queue coreSocialQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreSocialQueue).to(exchange).with(ROUTING_KEY_SOCIAL);
    }

    @Bean
    public Binding bindingAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(ROUTING_KEY_ANALYTICS);
    }

    @Bean
    public Binding bindingRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(ROUTING_KEY_RECOMMENDATIONS);
    }

    // Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Template para enviar mensajes (usado por el Core)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

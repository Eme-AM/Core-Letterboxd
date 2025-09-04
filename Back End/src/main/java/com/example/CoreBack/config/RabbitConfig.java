package com.example.CoreBack.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "letterboxd_exchange";

    // Queues
    public static final String CORE_ALL_QUEUE = "core.all.queue";
    public static final String CORE_USERS_QUEUE = "core.users.queue";
    public static final String CORE_MOVIES_QUEUE = "core.movies.queue";
    public static final String CORE_RATINGS_QUEUE = "core.ratings.queue";
    public static final String CORE_SOCIAL_QUEUE = "core.social.queue";

    // Routing keys
    public static final String ROUTING_KEY_ALL = "#";           // todos los eventos
    public static final String ROUTING_KEY_USERS = "user.*";    // eventos de usuarios
    public static final String ROUTING_KEY_MOVIES = "movie.*";  // eventos de películas
    public static final String ROUTING_KEY_RATINGS = "rating.*";// eventos de ratings
    public static final String ROUTING_KEY_SOCIAL = "social.*"; // eventos de social graph

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Declaración de colas
    @Bean
    public Queue coreAllQueue() {
        return new Queue(CORE_ALL_QUEUE, true);
    }

    @Bean
    public Queue coreUsersQueue() {
        return new Queue(CORE_USERS_QUEUE, true);
    }

    @Bean
    public Queue coreMoviesQueue() {
        return new Queue(CORE_MOVIES_QUEUE, true);
    }

    @Bean
    public Queue coreRatingsQueue() {
        return new Queue(CORE_RATINGS_QUEUE, true);
    }

    @Bean
    public Queue coreSocialQueue() {
        return new Queue(CORE_SOCIAL_QUEUE, true);
    }

    // Bindings
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

    // Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}

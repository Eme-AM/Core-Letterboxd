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

    // Colas
    public static final String CORE_ALL_QUEUE = "core.all.queue";
    public static final String CORE_USERS_QUEUE = "core.users.queue";
    public static final String CORE_MOVIES_QUEUE = "core.movies.queue";
    public static final String CORE_RATINGS_QUEUE = "core.ratings.queue";
    public static final String CORE_SOCIAL_QUEUE = "core.social.queue";
    public static final String CORE_ANALYTICS_QUEUE = "core.analytics.queue";
    public static final String CORE_RECOMMENDATIONS_QUEUE = "core.recommendations.queue";

    // Routing keys base
    public static final String RK_MOVIE = "peliculas.*";
    public static final String RK_USER = "usuarios.*";
    public static final String RK_RATING = "reseñas.*";
    public static final String RK_DISCOVERY = "discovery.*";
    public static final String RK_SOCIAL = "social.*";
    public static final String RK_ALL = "#"; // recibe todo

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // === Declaración de colas ===
    @Bean public Queue coreAllQueue() { return new Queue(CORE_ALL_QUEUE, true); }
    @Bean public Queue coreUsersQueue() { return new Queue(CORE_USERS_QUEUE, true); }
    @Bean public Queue coreMoviesQueue() { return new Queue(CORE_MOVIES_QUEUE, true); }
    @Bean public Queue coreRatingsQueue() { return new Queue(CORE_RATINGS_QUEUE, true); }
    @Bean public Queue coreSocialQueue() { return new Queue(CORE_SOCIAL_QUEUE, true); }
    @Bean public Queue coreAnalyticsQueue() { return new Queue(CORE_ANALYTICS_QUEUE, true); }
    @Bean public Queue coreRecommendationsQueue() { return new Queue(CORE_RECOMMENDATIONS_QUEUE, true); }

    // === Bindings automáticos según las relaciones que pasaste ===

    // 📦 Todos los eventos van también a "core.all.queue"
    @Bean
    public Binding bindingAll(Queue coreAllQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAllQueue).to(exchange).with(RK_ALL);
    }

    // 🎬 Películas → Ratings, Analytics, Recommendations, Social
    @Bean
    public Binding movieToRatings(Queue coreRatingsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRatingsQueue).to(exchange).with(RK_MOVIE);
    }
    @Bean
    public Binding movieToAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(RK_MOVIE);
    }
    @Bean
    public Binding movieToRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(RK_MOVIE);
    }
    @Bean
    public Binding movieToSocial(Queue coreSocialQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreSocialQueue).to(exchange).with(RK_MOVIE);
    }

    // 👤 Usuarios → Movies, Ratings, Analytics, Recommendations, Social
    @Bean
    public Binding userToMovies(Queue coreMoviesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreMoviesQueue).to(exchange).with(RK_USER);
    }
    @Bean
    public Binding userToRatings(Queue coreRatingsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRatingsQueue).to(exchange).with(RK_USER);
    }
    @Bean
    public Binding userToAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(RK_USER);
    }
    @Bean
    public Binding userToRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(RK_USER);
    }
    @Bean
    public Binding userToSocial(Queue coreSocialQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreSocialQueue).to(exchange).with(RK_USER);
    }

    // ⭐ Ratings/Reviews → Analytics, Recommendations, Social
    @Bean
    public Binding ratingToAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(RK_RATING);
    }
    @Bean
    public Binding reviewToAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(RK_RATING);
    }
    @Bean
    public Binding ratingToRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(RK_RATING);
    }
    @Bean
    public Binding reviewToRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(RK_RATING);
    }
    @Bean
    public Binding ratingToSocial(Queue coreSocialQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreSocialQueue).to(exchange).with(RK_RATING);
    }
    @Bean
    public Binding reviewToSocial(Queue coreSocialQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreSocialQueue).to(exchange).with(RK_RATING);
    }

    // 🤝 Social → Analytics, Recommendations, Ratings
    @Bean
    public Binding socialToAnalytics(Queue coreAnalyticsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreAnalyticsQueue).to(exchange).with(RK_SOCIAL);
    }
    @Bean
    public Binding socialToRecommendations(Queue coreRecommendationsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRecommendationsQueue).to(exchange).with(RK_SOCIAL);
    }
    @Bean
    public Binding socialToRatings(Queue coreRatingsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(coreRatingsQueue).to(exchange).with(RK_SOCIAL);
    }

    // === Conversor JSON y template ===
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}

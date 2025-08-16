package com.watchdog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer; // Import for RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis-related beans.
 * This class defines both the RedisTemplate for general data operations
 * and the RedisMessageListenerContainer for Pub/Sub functionality.
 */
@Configuration
public class RedisConfig {

    /**
     * Configures a RedisTemplate bean for performing various Redis operations.
     * It uses StringRedisSerializer for keys and GenericJackson2JsonRedisSerializer
     * for values to allow serialization of arbitrary Java objects to JSON in Redis.
     *
     * @param connectionFactory The RedisConnectionFactory automatically provided by Spring.
     * @return A configured RedisTemplate instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Serialize objects to JSON
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Defines the RedisMessageListenerContainer bean.
     * This container is essential for Redis Pub/Sub, allowing your application
     * to listen to messages published on Redis channels (e.g., for alert events).
     * Spring Boot's auto-configuration typically provides a RedisConnectionFactory
     * based on the 'spring.data.redis.*' properties in application.properties.
     *
     * @param connectionFactory The RedisConnectionFactory automatically provided by Spring.
     * @return A configured RedisMessageListenerContainer instance.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // You can configure a task executor for listeners here if you need more control
        // over how messages are processed (e.g., container.setTaskExecutor(yourExecutor);)
        return container;
    }
}

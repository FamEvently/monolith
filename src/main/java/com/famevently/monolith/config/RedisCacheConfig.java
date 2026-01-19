package com.famevently.monolith.config;

import com.famevently.monolith.cache.CacheEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class RedisCacheConfig {

    private static final String BASE_PACKAGE = "com.famevently.monolith";

    @Bean
    public CacheManager cacheManager(final RedisConnectionFactory connectionFactory,
                                     @Value("${spring.data.redis.default-ttl}") final Duration defaultTtl) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        final var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultTtl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        final var cacheConfigs = scanCacheEntities(serializer);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    private Map<String, RedisCacheConfiguration> scanCacheEntities(final Jackson2JsonRedisSerializer<Object> serializer) {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CacheEntity.class));

        return scanner.findCandidateComponents(BASE_PACKAGE).stream()
                .map(bd -> loadClass(bd.getBeanClassName()))
                .flatMap(Optional::stream)
                .map(clazz -> clazz.getAnnotation(CacheEntity.class))
                .reduce(
                        new HashMap<>(),
                        (configs, annotation) -> {
                            configs.put(
                                    annotation.cacheName(),
                                    buildCacheConfig(annotation, serializer)
                            );
                            return configs;
                        },
                        (a, b) -> { a.putAll(b); return a; }
                );
    }

    private RedisCacheConfiguration buildCacheConfig(final CacheEntity annotation, final Jackson2JsonRedisSerializer<Object> serializer) {
        final var ttl = Duration.of(annotation.ttl(), annotation.timeUnit().toChronoUnit());
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private Optional<Class<?>> loadClass(final String className) {
        try
        {
            return Optional.of(Class.forName(className));
        }
        catch (final ClassNotFoundException e)
        {
            return Optional.empty();
        }
    }
}

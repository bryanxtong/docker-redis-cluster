package example.springdataredis;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Configuration
public class LettuceConfig {
    @Bean
    @Primary
    RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        redisTemplate.setKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    ReactiveRedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        List<String> nodes = redisProperties.getCluster().getNodes();
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        nodes.forEach(s -> clusterConfiguration.addClusterNode(new RedisClusterNode(s.split(":")[0], Integer.valueOf(s.split(":")[1]))));
        return new LettuceConnectionFactory(clusterConfiguration, clientConfig);
    }

    @Bean
    RedisSerializationContext<Object, Object> redisSerializationContext() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
/*        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);*/
        RedisSerializationContext<Object, Object> redisSerializationContext = RedisSerializationContext.<Object, Object>newSerializationContext(serializer)
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();
        return redisSerializationContext;
    }

    @Bean
    @Primary
    ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate1(ReactiveRedisConnectionFactory lettuceConnectionFactory, RedisSerializationContext<Object, Object> redisSerializationContext) {
        ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate = new ReactiveRedisTemplate<>(lettuceConnectionFactory, redisSerializationContext);
        return reactiveRedisTemplate;
    }
}

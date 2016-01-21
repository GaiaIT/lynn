package net.gaiait.divination.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import net.gaiait.divination.symbol.persistence.PersistedSymbolEntity;

@Configuration
class RedisConfig {

    @Bean
    @Profile("dev")
    public RedisConnectionFactory devRedisCF() {
        return new JedisConnectionFactory();
    }
    
    @Bean
    @Profile("prod")
    public RedisConnectionFactory prodRedisCF(@Value("${redis_port}") int port, @Value("${redis_password}") String password) {
        JedisConnectionFactory cf = new JedisConnectionFactory();
        cf.setPort(port);
        cf.setPassword(password);
        return cf;
    }

    @Bean
    public RedisTemplate<String, PersistedSymbolEntity> redisSymbolTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, PersistedSymbolEntity> redis = new RedisTemplate<String, PersistedSymbolEntity>();
        redis.setConnectionFactory(cf);
        redis.setKeySerializer(new StringRedisSerializer());

        // We want to (de)serialize JSON with support for JDK8 optionals, so we get a bit custom
        // here...
        Jackson2JsonRedisSerializer<PersistedSymbolEntity> serializer =
                new Jackson2JsonRedisSerializer<PersistedSymbolEntity>(PersistedSymbolEntity.class);
        serializer.setObjectMapper(new ObjectMapper().registerModule(new Jdk8Module()));
        redis.setValueSerializer(serializer);

        return redis;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

    @Bean
    public RedisTemplate<String, Long> redisLongTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Long> redis = new RedisTemplate<String, Long>();
        redis.setConnectionFactory(cf);
        return redis;
    }

}

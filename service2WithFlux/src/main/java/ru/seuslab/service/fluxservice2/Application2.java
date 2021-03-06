package ru.seuslab.service.fluxservice2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

@ComponentScan(basePackages = {"ru.seuslab.service.fluxservice2.controller", "ru.seuslab.service.fluxservice2.service"})
@EnableJpaRepositories("ru.seuslab.service.fluxservice2.dao")
@EntityScan( basePackageClasses = { Application2.class, Jsr310JpaConverters.class })
@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class Application2 {

    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.port}")
    Integer port;

/*    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        log.info(host);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }*/
//LettuceClientConfiguration
/*    @Bean
    ReactiveRedisConnectionFactory jedisConnectionFactory() {
        log.info(host);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }*/

    @Bean
    ReactiveRedisOperations<String, List<Long>> redisOperations(ReactiveRedisConnectionFactory factory) {

        List<Long> list  = new ArrayList<>();

        Jackson2JsonRedisSerializer<List<Long>> serializer = new Jackson2JsonRedisSerializer(list.getClass());

        RedisSerializationContext.RedisSerializationContextBuilder<String, List<Long>> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, List<Long>> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }


    public static void main(String[] args) {
        SpringApplication.run(Application2.class, args);
    }
}
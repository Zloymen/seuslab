package ru.seuslab.service.service2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackages = {"ru.seuslab.service.service2.controller", "ru.seuslab.service.service2.service"})
@EnableJpaRepositories("ru.seuslab.service.service2.dao")
@EntityScan( basePackageClasses = { Application2.class, Jsr310JpaConverters.class })
@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class Application2 {

    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.port}")
    Integer port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        log.info(host);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application2.class, args);
    }
}
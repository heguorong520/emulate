package com.emulate.cache.redis.config;

import com.emulate.cache.redis.listener.LocalCacheRedisListener;
import com.emulate.cache.redis.serializer.FastJsonRedisSerializer;
import com.emulate.cache.redis.service.RedisService;
import com.emulate.cache.redis.util.RedisLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RedisConfig {

    //每个服务根据服务名订阅自己的消息主题
    @Value("${spring.application.name}")
    private String applicationName;

    @SuppressWarnings("all")
    @Bean(name = "redisTemplate")
    @ConditionalOnClass(RedisOperations.class)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 使用fastjson序列化
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnBean(name = "redisTemplate")
    public RedisService redisService(RedisTemplate redisTemplate, RedisLockUtil redisLockUtil) {
        return new RedisService(redisTemplate, redisLockUtil);
    }

    @Bean
    @ConditionalOnBean(name = "redisTemplate")
    public RedisLockUtil redisLockUtil(RedisTemplate redisTemplate) {
        return new RedisLockUtil(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(name = "redisTemplate")
    public LocalCacheRedisListener localCacheRedisListener() {
        return new LocalCacheRedisListener();
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(applicationName);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory
    ,LocalCacheRedisListener localCacheRedisListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(localCacheRedisListener, topic());
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setThreadNamePrefix("reidThreadPoolExecutor_");
        threadPoolExecutor.initialize();
        container.setTaskExecutor(threadPoolExecutor);

        return container;
    }

}

package com.example.msa.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public DefaultRedisScript<Long> participationLuaScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "local exists = redis.call('EXISTS', KEYS[1])\n" +
                "if exists == 0 then\n" +
                "  return -1\n" +
                "end\n" +
                "local current = tonumber(redis.call('HGET', KEYS[1], 'current'))\n" +
                "local max     = tonumber(redis.call('HGET', KEYS[1], 'max'))\n" +
                "if current == nil or max == nil then\n" +
                "  return -1\n" +
                "end\n" +
                "if current < max then\n" +
                "  redis.call('HINCRBY', KEYS[1], 'current', 1)\n" +
                "  return 1\n" +
                "else\n" +
                "  return 0\n" +
                "end"
        );
        return script;
    }
}

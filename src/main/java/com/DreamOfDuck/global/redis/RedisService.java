package com.DreamOfDuck.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, Object data, Long duration, TimeUnit timeUnit) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration, timeUnit);
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return (String)values.get(key);
    }

    public void setLongValue(String key, Long data, Long duration, TimeUnit timeUnit){
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (duration == null || timeUnit == null) {
            values.set(key, data); // TTL 없이 저장
        } else {
            values.set(key, data, duration, timeUnit); // TTL 적용
        }
    }
    public Long getLongValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return (Long)values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }
}

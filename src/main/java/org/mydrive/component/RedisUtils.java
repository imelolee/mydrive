package org.mydrive.component;

import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@Component("redisUtils")
public class RedisUtils<V> {
    @Resource
    private RedisTemplate<String, V> redisTemplate;

    public static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    public V get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, V value){
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e){
            logger.error("Set redisKey:(), value:() failed", key, value);
            return false;
        }
    }

    public boolean setex(String key, V value, long time){
        try {
            if (time > 0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else {
                set(key, value);
            }

            return true;
        } catch (Exception e){
            logger.error("Set redisKey:(), value:() failed", key, value);
            return false;
        }
    }
}

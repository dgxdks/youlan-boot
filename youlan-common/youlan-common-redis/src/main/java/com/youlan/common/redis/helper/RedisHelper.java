package com.youlan.common.redis.helper;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class RedisHelper {
    public final RedisTemplate<String, Object> redisTemplate;

    public Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long increment(final String key, final long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(final String key, final Object value, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void expire(final String key, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    public Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    public <T> T get(final String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (T) valueOperations.get(key);
    }

    public <T> Optional<T> getOpt(final String key) {
        return Optional.ofNullable(get(key));
    }

    public <T> List<T> get(Collection<String> keys) {
        return (List<T>) redisTemplate.opsForValue().multiGet(keys);
    }

    public Boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(final Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public void deleteByPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    public <T> Long setList(final String key, final List<T> list) {
        return rightPushAllList(key, list);
    }

    public <T> Long rightPushAllList(final String key, final List<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list.toArray());
    }

    public <T> Long leftPushAllList(final String key, final List<T> list) {
        return redisTemplate.opsForList().leftPushAll(key, list.toArray());
    }

    public <T> Long rightPushList(final String key, final T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public <T> Long leftPushList(final String key, final T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public <T> List<T> getList(final String key) {
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }

    public <T> T rightPopList(final String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    public <T> T leftPopList(final String key) {
        return (T) redisTemplate.opsForList().<T>leftPop(key);
    }

    public <T> Long setSet(final String key, final Set<T> set) {
        return redisTemplate.opsForSet().add(key, set.toArray());
    }

    public <T> T popSet(final String key) {
        return (T) redisTemplate.opsForSet().pop(key);
    }

    public <T> Set<T> getSet(final String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    public <T> void setMap(final String key, final Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public <T> Map<String, T> getMap(final String key) {
        return redisTemplate.<String, T>opsForHash().entries(key);
    }

    public <T> void setMapValue(final String key, final String hashKey, final T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public <T> T getMapValue(final String key, final String hashKey) {
        return redisTemplate.<String, T>opsForHash().get(key, hashKey);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}

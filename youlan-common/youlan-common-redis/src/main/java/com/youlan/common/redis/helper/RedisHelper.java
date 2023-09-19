package com.youlan.common.redis.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.youlan.common.redis.constant.RedisConstant.*;

public class RedisHelper {
    public static final RedisTemplate<String, Object> redisTemplate = SpringUtil.getBean(REDIS_TEMPLATE_BEAN_NAME);
    public static final RedisConnectionFactory redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);

    public static Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public static Long increment(final String key, final long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public static void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(final String key, final Object value, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public static void expire(final String key, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public static Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    public static Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    public static <T> T get(final String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (T) valueOperations.get(key);
    }

    public static <T> Optional<T> getOpt(final String key) {
        return Optional.ofNullable(get(key));
    }

    public static <T> List<T> get(Collection<String> keys) {
        return (List<T>) redisTemplate.opsForValue().multiGet(keys);
    }

    public static Boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    public static Long delete(final Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public static void deleteByPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
    }

    public static <T> Long setList(final String key, final List<T> list) {
        return rightPushAllList(key, list);
    }

    public static <T> Long rightPushAllList(final String key, final List<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list.toArray());
    }

    public static <T> Long leftPushAllList(final String key, final List<T> list) {
        return redisTemplate.opsForList().leftPushAll(key, list.toArray());
    }

    public static <T> Long rightPushList(final String key, final T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public static <T> Long leftPushList(final String key, final T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public static <T> List<T> getList(final String key) {
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }

    public static <T> T rightPopList(final String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    public static <T> T leftPopList(final String key) {
        return (T) redisTemplate.opsForList().<T>leftPop(key);
    }

    public static <T> Long setSet(final String key, final Set<T> set) {
        return redisTemplate.opsForSet().add(key, set.toArray());
    }

    public static <T> T popSet(final String key) {
        return (T) redisTemplate.opsForSet().pop(key);
    }

    public static <T> Set<T> getSet(final String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    public static <T> void setMap(final String key, final Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public static <T> Map<String, T> getMap(final String key) {
        return redisTemplate.<String, T>opsForHash().entries(key);
    }

    public static <T> void setMapValue(final String key, final String hashKey, final T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public static <T> T getMapValue(final String key, final String hashKey) {
        return redisTemplate.<String, T>opsForHash().get(key, hashKey);
    }

    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 获取Redis监控信息
     */
    public static Map<Object, Object> getMonitorInfo() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        Map<Object, Object> monitorInfo = new HashMap<>(3);
        monitorInfo.put(DB_SIZE, connection.dbSize());
        Properties info = connection.info();
        if (ObjectUtil.isNotNull(info)) {
            monitorInfo.putAll(info);
        }
        Properties commandProps = connection.info(COMMAND_STATS);
        if (ObjectUtil.isNotNull(commandProps)) {
            List<Map<String, String>> commandStats = new ArrayList<>();
            commandProps.stringPropertyNames().forEach(key -> {
                Map<String, String> data = new HashMap<>(2);
                String property = commandProps.getProperty(key);
                data.put("name", StrUtil.removePrefix(key, "cmdstat_"));
                data.put("value", StrUtil.subBetween(property, "calls=", ",usec"));
                commandStats.add(data);
            });
            monitorInfo.put(COMMAND_STATS, commandStats);
        }
        return monitorInfo;
    }
}

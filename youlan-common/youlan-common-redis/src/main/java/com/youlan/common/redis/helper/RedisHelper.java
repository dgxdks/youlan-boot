package com.youlan.common.redis.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import org.redisson.api.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.youlan.common.redis.constant.RedisConstant.*;

public class RedisHelper {

    @Getter
    private static final RedisTemplate<String, Object> redisTemplate = SpringUtil.getBean(REDIS_TEMPLATE_BEAN_NAME);

    @Getter
    private static final RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);

    @Getter
    private static final RedisConnectionFactory redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);

    /**
     * 设置缓存
     *
     * @param key   缓存键名
     * @param value 缓存键值
     */
    public static void setCacheObject(final String key, final Object value) {
        setCacheObject(key, value, false);
    }

    /**
     * 设置缓存
     *
     * @param key      缓存键名
     * @param value    缓存键值
     * @param duration 超时时间
     */
    public static void setCacheObject(final String key, final Object value, final Duration duration) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 设置缓存
     *
     * @param key   缓存键名
     * @param value 缓存键值
     */
    public static void setCacheObject(final String key, final Object value, final boolean saveTtl) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (saveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long timeToLive = bucket.remainTimeToLive();
                setCacheObject(key, value, Duration.ofMillis(timeToLive));
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param key   缓存键名
     * @param value 缓存键值
     */
    public static void setCacheObjectIfAbsent(final String key, final Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param key      缓存键名
     * @param value    缓存键值
     * @param duration 超时时间
     */
    public static void setCacheObjectIfAbsent(final String key, final Object value, Duration duration) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.setIfAbsent(value, duration);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键名
     * @return 缓存
     */
    public static <T> T getCacheObject(final String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键名
     */
    public static boolean deleteCacheObject(final String key) {
        return redissonClient.getBucket(key).delete();
    }

    /**
     * 删除缓存
     *
     * @param keys 缓存键名列表
     */
    public static void deleteCacheObject(final Collection<String> keys) {
        if (CollectionUtil.isEmpty(keys))
            return;
        RBatch batch = redissonClient.createBatch();
        keys.forEach(key -> batch.getBucket(key).deleteAsync());
        batch.execute();
    }

    /**
     * 设置缓存过期时间
     *
     * @param key      缓存键名
     * @param duration 过期时间
     */
    public static void expireCacheObject(final String key, final Duration duration) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.expire(duration);
    }

    /**
     * 获取缓存过期时间
     *
     * @param key 缓存键名
     * @return 过期时间(毫秒)
     */
    public static long getCacheExpire(final String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }

    /**
     * 缓存是否存在
     *
     * @param key 缓存键名
     * @return 是否存在
     */
    public static boolean cacheObjectExists(final String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 设置缓存List
     *
     * @param key  缓存键名
     * @param list 缓存List
     * @return 是否成功
     */
    public static boolean setCacheList(final String key, final Collection<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            return true;
        }
        RList<Object> rList = redissonClient.getList(key);
        return rList.addAll(list);
    }

    /**
     * 获取缓存List
     *
     * @param key 缓存键名
     * @return 缓存List
     */
    public static <T> List<T> getCacheList(final String key) {
        RList<T> rList = redissonClient.getList(key);
        return rList.readAll();
    }

    /**
     * 设置缓存Set
     *
     * @param key 缓存键名
     * @param set 缓存Set
     * @return 是否成功
     */
    public static boolean setCacheSet(final String key, Collection<?> set) {
        RSet<Object> rSet = redissonClient.getSet(key);
        return rSet.addAll(set);
    }

    /**
     * 获取缓存Set
     *
     * @param key 缓存键名
     * @return 缓存Set
     */
    public static <T> Set<T> getCacheSet(final String key) {
        RSet<T> rSet = redissonClient.getSet(key);
        return rSet.readAll();
    }

    /**
     * 设置缓存Map
     *
     * @param key 缓存键名
     * @param map 缓存Map
     */
    public static void setCacheMap(final String key, final Map<?, ?> map) {
        if (CollectionUtil.isEmpty(map)) {
            return;
        }
        RMap<Object, Object> rMap = redissonClient.getMap(key);
        rMap.putAll(map);
    }

    /**
     * 获取缓存Map
     *
     * @param key 缓存键名
     * @return 缓存Map
     */
    public static <K, V> Map<K, V> getCacheMap(final String key) {
        RMap<K, V> rMap = redissonClient.getMap(key);
        return rMap.readAllMap();
    }

    /**
     * 获取缓存Map
     *
     * @param key   缓存键名
     * @param hKeys 指定缓存Map中key集合
     * @return 缓存Map
     */
    public static <K, V> Map<K, V> getCacheMap(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = redissonClient.getMap(key);
        return rMap.getAll(hKeys);
    }

    /**
     * 获取缓存Map的key集合
     *
     * @param key 缓存键名
     */
    public static <T> Set<T> getCacheMapKeySet(final String key) {
        RMap<T, Object> rMap = redissonClient.getMap(key);
        return rMap.keySet();
    }

    /**
     * 设置缓存Map中指定key的值
     *
     * @param key    缓存键名
     * @param hKey   Map中的key
     * @param hValue Map中的value
     */
    public static void setCacheMapValue(final String key, final Object hKey, final Object hValue) {
        RMap<Object, Object> rMap = redissonClient.getMap(key);
        rMap.put(hKey, hValue);
    }

    /**
     * 获取缓存Map中指定key的值
     *
     * @param key  缓存键名
     * @param hKey Map中的key
     */
    public static <T> T getCacheMapValue(final String key, final Object hKey) {
        RMap<?, T> rMap = redissonClient.getMap(key);
        return rMap.get(hKey);
    }

    /**
     * 删除缓存Map中指定key的值
     *
     * @param key  缓存键名
     * @param hKey Map中的key
     */
    public static <T> T deleteCacheMapValue(final String key, final Object hKey) {
        RMap<?, T> rMap = redissonClient.getMap(key);
        return rMap.remove(hKey);
    }

    /**
     * 删除缓存Map中指定key的值
     *
     * @param key   缓存键名
     * @param hKeys Map中的key集合
     */
    public static void deleteCacheMapValue(final String key, final Collection<?> hKeys) {
        if (CollectionUtil.isEmpty(hKeys)) {
            return;
        }
        RBatch batch = redissonClient.createBatch();
        RMapAsync<Object, Object> rMap = batch.getMap(key);
        hKeys.forEach(rMap::removeAsync);
        batch.execute();
    }

    /**
     * 设置原子Long
     *
     * @param key   缓存键名
     * @param value Long值
     */
    public static void setAtomicLong(final String key, final long value) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.set(value);
    }

    /**
     * 获取原子Long
     *
     * @param key 缓存键名
     * @return Long值
     */
    public static long getAtomicLong(final String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.get();
    }

    /**
     * 递增原子Long
     *
     * @param key 缓存键名
     * @return 递增后的Long值
     */
    public static long incrementAtomicLong(final String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.incrementAndGet();
    }

    /**
     * 递减原子Long
     *
     * @param key 缓存键名
     * @return 递减后的Long值
     */
    public static long decrementAtomicLong(final String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.decrementAndGet();
    }

    /**
     * 根据匹配规则获取缓存键名集合
     *
     * @param pattern 匹配规则
     * @return 键名集合
     */
    public static List<String> getKeysByPattern(final String pattern) {
        RKeys keys = redissonClient.getKeys();
        return keys.getKeysStreamByPattern(pattern).collect(Collectors.toList());
    }

    /**
     * 根据匹配规则删除缓存
     *
     * @param pattern 匹配规则
     * @return 删除条数
     */
    public static long deleteKeysByPattern(final String pattern) {
        RKeys keys = redissonClient.getKeys();
        return keys.deleteByPattern(pattern);
    }

    /**
     * 获取限流操作对象
     *
     * @param key 限流键名
     * @return 限流操作对象
     */
    public static RRateLimiter getRateLimiter(final String key) {
        return redissonClient.getRateLimiter(key);
    }

    /**
     * 锁下操作
     *
     * @param key      缓存键名
     * @param runnable 业务逻辑
     * @param duration 锁超时时间
     */
    public static void underLock(final String key, Runnable runnable, final Duration duration) {
        RLock lock = getLock(key);
        try {
            lock.lock(duration.toMillis(), TimeUnit.MILLISECONDS);
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 锁下操作
     *
     * @param key      缓存键名
     * @param supplier 业务逻辑
     * @param duration 锁超时时间
     */
    public static <T> T underLock(final String key, Supplier<T> supplier, final Duration duration) {
        RLock lock = getLock(key);
        try {
            lock.lock(duration.toMillis(), TimeUnit.MILLISECONDS);
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取锁对象
     *
     * @param key 缓存键名
     * @return 锁对象
     */
    public static RLock getLock(final String key) {
        return redissonClient.getLock(key);
    }

    /**
     * 获取Redis监控信息
     *
     * @return Redis监控信息
     */
    public static Map<Object, Object> getRedisMonitorInfo() {
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

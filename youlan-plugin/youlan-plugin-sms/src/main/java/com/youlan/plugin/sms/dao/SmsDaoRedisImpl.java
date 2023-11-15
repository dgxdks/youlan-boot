package com.youlan.plugin.sms.dao;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.sms.utils.SmsUtil;
import org.dromara.sms4j.api.dao.SmsDao;

import java.time.Duration;

public class SmsDaoRedisImpl implements SmsDao {

    @Override
    public void set(String key, Object value, long cacheTime) {
        RedisHelper.setCacheObject(SmsUtil.getSmsDaoRedisKey(key), value, Duration.ofSeconds(cacheTime));
    }

    @Override
    public void set(String key, Object value) {
        RedisHelper.setCacheObject(SmsUtil.getSmsDaoRedisKey(key), value);
    }

    @Override
    public Object get(String key) {
        return RedisHelper.getCacheObject(SmsUtil.getSmsDaoRedisKey(key));
    }

    @Override
    public void clean() {
        RedisHelper.deleteKeysByPattern(SmsUtil.getSmsDaoRedisKey(StringPool.ASTERISK));
    }
}

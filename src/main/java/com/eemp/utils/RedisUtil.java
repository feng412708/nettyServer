package com.eemp.utils;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by A03742 on 2017-07-17.
 */

public class RedisUtil {

    //redis 操作属性
    public RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Logger log = Logger.getLogger(RedisUtil.class.getName());

    /**
     * 设置字符串到Redis缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean setStringValue(String key, String value) {
        return setStringValue(key, value, -1);
    }

    /**
     * 设置字符串到Redis缓存,设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  失效时间
     */
    public boolean setStringValue(String key, String value, long time) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, value);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + value + "]", t);
        }
        return false;

    }

    /**
     * 从redis 缓存获取字符串值
     *
     * @param key 键
     * @return String值
     */
    public String getStringValue(String key) {
        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            return valueOps.get(key);
        } catch (Throwable t) {
            log.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }


    /**
     * 设置字符串到Redis缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean setIntValue(String key, int value) {
        return setIntValue(key, value, -1);
    }

    /**
     * 设置字符串到Redis缓存,设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  失效时间 单位：秒
     */
    public boolean setIntValue(String key, int value, long time) {
        try {
            ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, value);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + value + "]", t);
        }
        return false;
    }

    /**
     * 从redis 缓存获取字符串值
     *
     * @param key 键
     * @return String值
     */
    public Integer getIntValue(String key) {
        try {
            ValueOperations<String, Integer> valueOps = redisTemplate.opsForValue();
            return valueOps.get(key);
        } catch (Throwable t) {
            log.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }


    /**
     * 设置double到Redis缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean setDoubleValue(String key, double value) {
        return setDoubleValue(key, value, -1);
    }

    /**
     * 设置double到Redis缓存,设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  失效时间 单位：秒
     */
    public boolean setDoubleValue(String key, double value, long time) {
        try {
            ValueOperations<String, Double> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, value);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + value + "]", t);
        }
        return false;
    }

    /**
     * 从redis 缓存获取Double值
     *
     * @param key 键
     * @return String值
     */
    public Double getDoubleValue(String key) {
        try {
            ValueOperations<String, Double> valueOps = redisTemplate.opsForValue();
            return valueOps.get(key);
        } catch (Throwable t) {
            log.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * 设置 list到缓存
     *
     * @param key  键
     * @param list 值
     * @return 布尔型 true：成功 false:失败
     */
    public boolean setList(String key, List list) {
        return setList(key, list, -1);
    }

    /**
     * 设置 list到缓存
     *
     * @param key   键
     * @param value 值
     * @param time  失效时间 秒
     * @return 布尔型 true：成功 false:失败
     */
    public boolean setList(String key, List value, long time) {

        try {
            ListOperations listOps = redisTemplate.opsForList();
            listOps.rightPushAll(key, value);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + value + "]", t);
        }
        return false;
    }


    /**
     * 获取list 的大小
     *
     * @param key 键
     * @return long list大小
     */
    public long getListSize(String key) {
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.size(key);
        } catch (Throwable t) {
            log.error("获取list长度失败key[" + key + "], error[" + t + "]");
        }
        return 0;
    }

    /**
     * 获取List 对象
     *
     * @param key   键
     * @param start 开始下标
     * @param end   结束下标
     * @return List
     */
    public List getList(String key, long start, long end) {
        try {
            ListOperations<String, Serializable> listOps = redisTemplate.opsForList();
            return listOps.range(key, start, end);
        } catch (Throwable t) {
            log.error("获取list缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * 缓存set操作
     *
     * @param key  键
     * @param v    值
     * @param time 失效时间，秒
     * @return 布尔型 true：成功 false：失败
     */
    protected boolean cacheSet(String key, String v, long time) {
        try {
            SetOperations<String, String> valueOps = redisTemplate.opsForSet();
            valueOps.add(key, v);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     * 缓存set
     *
     * @param k 键
     * @param v 值
     * @return 布尔型 true：成功 false：失败
     */
    protected boolean cacheSet(String k, String v) {
        return cacheSet(k, v, -1);
    }

    /**
     * 缓存set
     *
     * @param key  键
     * @param v    值
     * @param time 失效时间
     * @return 布尔型 true：成功 false：失败
     */
    protected boolean cacheSet(String key, Set<String> v, long time) {

        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            setOps.add(key, v.toArray(new String[v.size()]));
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } catch (Throwable t) {
            log.error("缓存[" + key + "]失败, value[" + v + "]", t);
        }
        return false;
    }

    /**
     * 缓存set
     *
     * @param key 键
     * @param v   值
     * @return 布尔型 true：成功 false：失败
     */
    protected boolean cacheSet(String key, Set<String> v) {
        return cacheSet(key, v, -1);
    }

    /**
     * 获取缓存set数据
     *
     * @param key 键
     * @return Set
     */
    protected Set<String> getSet(String key) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.members(key);
        } catch (Throwable t) {
            log.error("获取set缓存失败key[" + key + ", error[" + t + "]");
        }
        return null;
    }


    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return boolean true:key存在 false:key 不存在
     */
    public boolean containsKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
            log.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }

    /**
     * 移除缓存
     *
     * @param key 键
     * @return boolean true:移除成功 false:移除失败
     */
    public boolean remove(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            log.error("获取缓存失败key[" + key + ", error[" + t + "]");
        }
        return false;
    }


}

package com.eemp.utils;

import com.sun.deploy.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by A03742 on 2018-04-28.
 */
public class JedisUtils {

    protected static ReentrantLock lockPool = new ReentrantLock();
    protected static ReentrantLock lockJedis = new ReentrantLock();

    protected static Logger logger = Logger.getLogger(JedisUtils.class);
    static  Properties prop = new Properties();
    static {


        try {
            String path = JedisUtils.class.getResource("/").getPath();
            System.out.println(path);
            InputStream in = new BufferedInputStream(new FileInputStream(
                    new File( path+"/prop/config.local.prop")));
            prop.load(in);
        }catch (Exception e){
            e.printStackTrace();
        }


        String host = prop.getProperty("redis.host");
       System.out.println(host);
    }


    //Redis服务器IP
    private static String ADDR_ARRAY = prop.getProperty("redis.host");

    //Redis的端口号
    private static int PORT = Integer.parseInt(prop.getProperty("redis.port"));

    //访问密码
    private static String AUTH = "";
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 8;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 8;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 3000;

    //超时时间
    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = false;

    private static JedisPool jedisPool = null;

    /**
     * redis过期时间,以秒为单位
     */
    public final static int EXRP_HOUR = 60 * 60;            //一小时
    public final static int EXRP_DAY = 60 * 60 * 24;        //一天
    public final static int EXRP_MONTH = 60 * 60 * 24 * 30; //一个月

    /**
     * 初始化Redis连接池
     */
    private static void initialPool() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT);
        } catch (Exception e) {
            logger.error("First create JedisPool error : " + e);
            try {
                //如果第一个IP异常，则访问第二个IP
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(MAX_ACTIVE);
                config.setMaxIdle(MAX_IDLE);
                config.setMaxWaitMillis(MAX_WAIT);
                config.setTestOnBorrow(TEST_ON_BORROW);
                jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT, AUTH);
            } catch (Exception e2) {
                logger.error("Second create JedisPool error : " + e2);
            }
        }
    }

    /**
     * 在多线程环境同步初始化
     */
    private static void poolInit() {
        lockPool.lock();
        try {
            if (jedisPool == null) {
                initialPool();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockPool.unlock();
        }
    }

    public static Jedis getJedis() {
        lockJedis.lock();
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : " + e);
        } finally {
            returnResource(jedis);
            lockJedis.unlock();
        }
        return jedis;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            //jedisPool.returnResource(jedis);
            jedis.close();
        }
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public synchronized static void setString(String key, String value) {
        try {
            value = (value==null) ? "" : value;
            getJedis().set(key, value);
        } catch (Exception e) {
            logger.error("Set key error : " + e);
        }
    }

    /**
     * 设置 过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     * @param value
     */
    public synchronized static void setString(String key, int seconds, String value) {
        try {
            value = (null == value) ? "" : value;
            getJedis().setex(key, seconds, value);
        } catch (Exception e) {
            logger.error("Set keyex error : " + e);
        }
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public synchronized static String getString(String key) {
        Jedis jedis = getJedis();
        if (jedis == null || !jedis.exists(key)) {
            return null;
        }
        return jedis.get(key);
    }
}

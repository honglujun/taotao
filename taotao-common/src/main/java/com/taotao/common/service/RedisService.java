package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {

    // 如果Spring容器中有，就注入，如果没有，就不注入
    @Autowired(required = false)
    private ShardedJedisPool jedisPool;

    private <T> T execute(Function<ShardedJedis, T> fun) {
        ShardedJedis shardedJedis = null;
        try {
            // 从连接池中获取到jedis分片对象
            shardedJedis = jedisPool.getResource();
            return fun.callback(shardedJedis);
        } finally {
            if (null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
    }

    /**
     * 执行SET操作
     * 
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public String set(final String key, final String value) throws Exception {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.set(key, value);
            }
        });
    }

    /**
     * 设置值并且设置生存时间，单位为秒
     * 
     * @param key
     * @param value
     * @param seconds
     * @return
     * @throws Exception
     */
    public String set(final String key, final String value, final Integer seconds) throws Exception {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis e) {
                String str = e.set(key, value);
                e.expire(key, seconds);
                return str;
            }
        });
    }
    
    /**
     * 设置生存时间
     * 
     * @param key
     * @param seconds
     * @return
     * @throws Exception
     */
    public Long expire(final String key, final Integer seconds) throws Exception {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.expire(key, seconds);
            }
        });
    }

    /**
     * 执行GET操作
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public String get(final String key) throws Exception {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.get(key);
            }
        });
    }

    /**
     * 删除一个键
     * 
     * @param key
     * @return
     */
    public Long del(final String key) {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.del(key);
            }
        });
    }

}

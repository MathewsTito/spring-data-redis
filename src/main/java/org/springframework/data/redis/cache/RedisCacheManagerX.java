package org.springframework.data.redis.cache;

import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;

/**
 * Enhanced version of RedisCacheManager that can be configured with two different RedisOperations, one of which will
 * be used only for "get" operations on the cache. This will allow usage of an independent connection pool for "get"
 * operations.
 *
 * @author Tito Mathews
 *
 */
public class RedisCacheManagerX extends RedisCacheManager{
    RedisOperations redisOperationsForGet;

    public RedisCacheManagerX(RedisOperations redisOperations, RedisOperations redisOperationsForGet) {
        super(redisOperations);
        this.redisOperationsForGet = redisOperationsForGet;
    }

    public RedisCacheManagerX(RedisOperations redisOperations, RedisOperations redisOperationsForGet,Collection<String> cacheNames) {
        super(redisOperations,cacheNames);
        this.redisOperationsForGet = redisOperationsForGet;
    }

    @SuppressWarnings("unchecked")
    protected RedisCache createCache(String cacheName) {
        long expiration = computeExpiration(cacheName);
        boolean usePrefix = isUsePrefix();
        return new RedisCacheX(cacheName, (usePrefix?getCachePrefix().prefix(cacheName):null), getRedisOperations(),redisOperationsForGet, expiration);
    }
}

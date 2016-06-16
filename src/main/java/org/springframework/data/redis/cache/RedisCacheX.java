package org.springframework.data.redis.cache;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.dao.DataAccessException;

/**
 *
 * Augmented RedisCache that overrides the "get" method to work with a different RedisOperation instance
 *
 * @author Tito Mathews
 *
 */

public class RedisCacheX extends RedisCache{

    @SuppressWarnings("rawtypes")//
    private final RedisOperations redisOperationsForGet;
    private final RedisCacheMetadata cacheMetadata;
    private final CacheValueAccessor cacheValueAccessor;

    public RedisCacheX(String name, byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations, RedisOperations<? extends Object, ? extends Object> redisOperationsForGet, long expiration) {
        super(name, prefix, redisOperations, expiration);
        this.cacheMetadata = new RedisCacheMetadata(name, prefix);
        this.cacheMetadata.setDefaultExpiration(expiration);

        this.redisOperationsForGet = redisOperationsForGet;
        this.cacheValueAccessor = new CacheValueAccessor(redisOperationsForGet.getValueSerializer());
    }

    /**
     * Return the value to which this cache maps the specified key.
     *
     * @param cacheKey the key whose associated value is to be returned via its binary representation.
     * @return the {@link RedisCacheElement} stored at given key or {@literal null} if no value found for key.
     * @since 1.5
     */
    public RedisCacheElement get(final RedisCacheKey cacheKey) {

        //notNull(cacheKey, "CacheKey must not be null!");

        byte[] bytes = (byte[]) redisOperationsForGet.execute(new AbstractRedisCacheCallback<byte[]>(new BinaryRedisCacheElement(
                new RedisCacheElement(cacheKey, null), cacheValueAccessor), cacheMetadata) {

            @Override
            public byte[] doInRedis(BinaryRedisCacheElement element, RedisConnection connection) throws DataAccessException {
                return connection.get(element.getKeyBytes());
            }
        });

        return (bytes == null ? null : new RedisCacheElement(cacheKey, cacheValueAccessor.deserializeIfNecessary(bytes)));
    }
}
package com.famevently.monolith.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to mark an entity as cacheable in Redis.
 * Use this in conjunction with @Cacheable on repository methods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEntity {

    /**
     * The cache name to use for this entity.
     * This should match the 'value' in @Cacheable annotation.
     */
    String cacheName();

    /**
     * Time-to-live value for cached entries.
     * Default is 1 hour.
     */
    long ttl() default 1;

    /**
     * Time unit for the TTL value.
     * Default is HOURS.
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;
}

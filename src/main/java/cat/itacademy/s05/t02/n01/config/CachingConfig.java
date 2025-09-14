package cat.itacademy.s05.t02.n01.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        // ConcurrentMapCacheManager: simple, sin dependencias externas.
        // Caches usadas por anotaciones @Cacheable/@CachePut/@CacheEvict
        return new ConcurrentMapCacheManager(
                "professionalProfiles"
        );
    }
}


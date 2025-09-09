package idc.inbound.configuration;


import idc.inbound.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheListener {

    private final RedisCacheService redis;

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2.0, maxDelay = 3000)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvict(UserCacheEvictEvent ev) {
        for (String field : ev.fields()) {
            redis.deleteFromHash(ev.hashName(), field);
        }
        log.debug("Evicted fields {} from hash {}", ev.fields(), ev.hashName());
    }

    @Recover
    public void recover(Exception ex, UserCacheEvictEvent ev) {
        log.error("FAILED to evict {} from {} after retries: {}", ev.fields(), ev.hashName(), ex.toString());
    }
}

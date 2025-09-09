package idc.inbound.configuration;

import idc.inbound.dto.BookingCacheUpdateEvent;
import idc.inbound.dto.RedisBookingDTO;
import idc.inbound.redis.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCacheListener {

    private final RedisCacheService redis;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 200, multiplier = 2.0, maxDelay = 3000))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BookingCacheUpdateEvent ev) {
        for (Long id : ev.bookingIds()) {
            RedisBookingDTO dto = RedisBookingDTO.builder()
                    .id(id)
                    .whoProcessingId(ev.userId())
                    .actualStatus(ev.statusCode())
                    .isStart(ev.started())
                    .build();

            redis.saveToCacheWithTTL(String.format("booking:%d:idc", dto.id), dto, Duration.ofMinutes(15));
        }
    }

    @Recover
    public void recover(Throwable ex, BookingCacheUpdateEvent ev) {
        log.error("Redis update failed for {}: {}", ev.bookingIds(), ex.toString());
    }

}

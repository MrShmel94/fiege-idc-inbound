package idc.inbound.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import idc.inbound.dto.RedisBookingDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate srt;
    private final ObjectMapper objectMapper;

    public void setInt(String key, int value, Duration ttl) {
        srt.opsForValue().set(key, Integer.toString(value), ttl);
    }

    public OptionalInt getInt(String key) {
        String v = srt.opsForValue().get(key);
        return v == null ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(v));
    }

    public void incr(String key) { srt.opsForValue().increment(key); }
    public void delete(String key) { srt.delete(key); }

    public <T> void saveToCache(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void saveToCacheWithTTL(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public <T> Optional<T> getFromCache(String key, Class<T> type) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return Optional.empty();
        }

        try {
            T value = objectMapper.convertValue(cachedValue, type);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting cached value: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String bookingKey(long id) {
        return "booking:%d:idc".formatted(id);
    }

    public <T> Map<Long, T> multiGetBookingsByIds(List<Long> ids, Class<T> type) {
        List<String> keys = ids.stream().map(this::bookingKey).toList();
        List<Object> raw = redisTemplate.opsForValue().multiGet(keys);

        Map<Long, T> out = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            Object v = raw.get(i);
            if (v == null) continue;

            T val = type.isInstance(v) ? type.cast(v) : objectMapper.convertValue(v, type);

            if (val instanceof RedisBookingDTO dto) {
                @SuppressWarnings("unchecked")
                T normalized = (T) normalizeBooking(dto);
                out.put(ids.get(i), normalized);
            } else {
                out.put(ids.get(i), val);
            }
        }
        return out;
    }

    private RedisBookingDTO normalizeBooking(RedisBookingDTO src) {
        return RedisBookingDTO.builder()
                .id(src.getId())
                .actualStatus(src.getActualStatus() != null ? src.getActualStatus() : "Unknown")
                .isStart(src.isStart())
                .whoProcessingId(src.getWhoProcessingId())
                .build();
    }

    public void removeFromCache(String key) {
        redisTemplate.delete(key);
    }

    public <T> Optional<T> getFromCache(String key, TypeReference<T> typeReference) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return Optional.empty();
        }

        try {
            T value = objectMapper.convertValue(cachedValue, typeReference);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting cached value: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public <T> Optional<T> getFromHash(String hashName, String fieldKey, Class<T> type) {
        Object cachedValue = redisTemplate.opsForHash().get(hashName, fieldKey);
        if (cachedValue == null) {
            return Optional.empty();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            T value = mapper.convertValue(cachedValue, type);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting value from hash {} field {}: {}", hashName, fieldKey, e.getMessage());
            return Optional.empty();
        }
    }

    public <T> Map<String, T> getAllFromHash(String hashName, TypeReference<T> typeReference) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashName);
        if (entries.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, T> result = new HashMap<>();
        entries.forEach((key, value) -> {
            try {
                T convertedValue = objectMapper.convertValue(value, typeReference);
                result.put((String) key, convertedValue);
            } catch (IllegalArgumentException e) {
                log.error("Error converting value from hash {} key {}: {}", hashName, key, e.getMessage());
            }
        });

        return result;
    }

    public <T> void saveToHash(String hashName, String fieldKey, T value) {
        redisTemplate.opsForHash().put(hashName, fieldKey, value);
    }

    public <T> void saveAllToHash(String hashName, Map<String, T> values) {
        redisTemplate.opsForHash().putAll(hashName, values);
    }

    public void deleteFromHash(String hashName, String fieldKey) {
        redisTemplate.opsForHash().delete(hashName, fieldKey);
    }

}

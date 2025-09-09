package idc.inbound.configuration;

import java.util.List;

public record UserCacheEvictEvent(
        String hashName,
        List<String> fields
) {
    public static UserCacheEvictEvent of(String hash, String singleField) {
        return new UserCacheEvictEvent(hash, List.of(singleField));
    }
}

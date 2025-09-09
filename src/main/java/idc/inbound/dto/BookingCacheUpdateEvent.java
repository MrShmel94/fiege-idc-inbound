package idc.inbound.dto;

import java.util.List;

public record BookingCacheUpdateEvent(
        List<Long> bookingIds,
        Integer userId,
        String statusCode,
        boolean started
) {}
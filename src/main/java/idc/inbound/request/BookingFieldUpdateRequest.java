package idc.inbound.request;

import java.time.LocalDate;

public record BookingFieldUpdateRequest(
        Long recordId,
        String field,
        String value,
        LocalDate date
) {
}

package idc.inbound.request;

import java.time.LocalDate;
import java.util.List;

public record ForkliftControlUpdate(
        String type,
        String action,
        Integer value,
        LocalDate dateBooking,
        List<Long> bookingIds,
        String comment,
        LocalDate timestamp
) {
}

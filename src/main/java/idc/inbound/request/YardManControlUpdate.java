package idc.inbound.request;

import java.time.LocalDate;
import java.util.List;

public record YardManControlUpdate(
        String type,
        String action,
        LocalDate dateBooking,
        String value,
        Long bookingId
) {
}

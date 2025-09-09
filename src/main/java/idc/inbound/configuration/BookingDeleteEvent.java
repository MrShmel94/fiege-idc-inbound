package idc.inbound.configuration;

import java.time.LocalDate;
import java.util.List;

public record BookingDeleteEvent(List<Long> bookingIds, String type, LocalDate date, List<String> topics) {
    public BookingDeleteEvent(Long bookingId, String type, LocalDate date, List<String> topics) {
        this(List.of(bookingId), type, date, topics);
    }
}

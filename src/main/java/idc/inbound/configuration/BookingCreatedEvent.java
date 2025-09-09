package idc.inbound.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public record BookingCreatedEvent(List<Long> bookingIds, String type, LocalDate date, List<String> topics) {
    public BookingCreatedEvent(Long bookingId, String type, LocalDate date, List<String> topics) {
        this(List.of(bookingId), type, date, topics);
    }
}
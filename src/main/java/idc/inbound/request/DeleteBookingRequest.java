package idc.inbound.request;

import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record DeleteBookingRequest(
        @Min(1) Long id,
        LocalDate date
) {
}

package idc.inbound.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnloadingSaveBramRampRequest(
        @NotBlank String name,
        @NotNull @Min(1) Integer maxBuffer
) {
}

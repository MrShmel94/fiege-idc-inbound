package idc.inbound.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnloadingChangeBramRampRequest(
        @NotNull Integer id,
        @NotBlank String oldName,
        @NotBlank String newName,
        @NotBlank String status,
        @NotNull @Min(1) Integer maxBuffer,
        @NotNull @Min(0) Integer currentBuffer
) {
}

package idc.inbound.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnloadingChangeRequestModal(
        @NotBlank String oldName,
        @NotNull Integer id,
        @NotBlank String getNewName
) implements NameChangeRequest {

    @Override
    public Integer getId() {
        return this.id;
    }
}

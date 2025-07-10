package idc.inbound.request;

import jakarta.validation.constraints.NotBlank;

public record UnloadingRequestModal(
        @NotBlank String getNewName
) implements NameChangeRequest{

    @Override
    public Integer getId() {
        return -1;
    }
}

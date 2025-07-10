package idc.inbound.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserFirstLoginRequestModel(
        @Size(min = 5, message = "Login must be at least 5 characters")
        @NotBlank(message = "Login cannot be empty")
        String login,

        @Size(min = 8, message = "First Password must be at least 8 characters")
        @NotBlank(message = "First Password cannot be empty")
        @ValidPassword
        String firstPassword,

        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password cannot be empty")
        @ValidPassword
        String password
) {
}

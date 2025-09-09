package idc.inbound.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest{
    @NotBlank @Size(min = 2) private String name;
    @NotBlank @Size(min = 2) private String secondName;
    @NotBlank @Size(min = 5) private String expertis;
    @NotBlank @Size(min = 5) private String oldExpertis;
    @NotBlank @Size(min = 5) private String login;
    @NotBlank @Size(min = 5) private String oldLogin;
    @NotNull @Min(1) private int positionId;
    @NotNull @Min(1) private int departmentId;
    @NotNull @Min(1) private int roleId;
}

package idc.inbound.dto.vision;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String name;
    private String secondName;
    private String expertis;
    private String login;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isFirstLogin;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isActive;
    private String positionName;
    private String departmentName;
    private String createdByUserName;
    private String roleName;
    private Integer roleWeight;
}

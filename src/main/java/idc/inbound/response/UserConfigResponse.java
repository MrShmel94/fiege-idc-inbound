package idc.inbound.response;

import idc.inbound.dto.vision.DepartmentDTO;
import idc.inbound.dto.vision.PositionDTO;
import idc.inbound.dto.vision.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfigResponse {
    List<DepartmentDTO> departments;
    List<PositionDTO> positions;
    List<RoleDTO> roles;
}

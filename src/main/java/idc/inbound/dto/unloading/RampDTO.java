package idc.inbound.dto.unloading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RampDTO {
    private Integer id;
    private String name;
    private String status;
    private Integer maxBuffer;
    private Integer actualBuffer;
}

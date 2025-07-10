package idc.inbound.dto.unloading;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BramDTO {
    private Integer id;
    private String name;
    private String status;

    @JsonInclude()
    private Integer maxBuffer;
    @JsonInclude()
    private Integer actualBuffer;
}

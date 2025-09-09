package idc.inbound.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.jackson.Jacksonized;


@Getter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedisBookingDTO {
    public long id;
    public Integer whoProcessingId;
    public boolean isStart;
    private String rampName;
    public String actualStatus;
}

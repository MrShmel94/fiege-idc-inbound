package idc.inbound.dto.unloading;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForkliftDTO {

    private Long id;
    @JsonInclude()
    private LocalDate date;
    @JsonInclude()
    private Integer ramp;
    @JsonInclude()
    private String rampName;
    @JsonInclude()
    private Integer bram;
    @JsonInclude()
    private String bramName;
    @JsonInclude()
    private Integer status;
    @JsonInclude()
    private String statusName;
    @JsonInclude()
    private Integer deliveryType;
    @JsonInclude()
    private String deliveryTypeName;
    @JsonInclude()
    private Integer qtyPal;
    @JsonInclude()
    private Integer qtyBox;
    @JsonInclude()
    private Integer qtyItems;
    @JsonInclude()
    private LocalTime estimatedArrivalTime;
    @JsonInclude()
    private LocalTime arrivalTime;
    @JsonInclude()
    private String notificationNumber;
    @JsonInclude()
    private String bookingId;
    @JsonInclude()
    private Integer productType;
    @JsonInclude()
    private String productTypeName;
    @JsonInclude()
    private Integer actualColi;
    @JsonInclude()
    private Integer actualEuPal;
    @JsonInclude()
    private Integer actualEuPalDefect;
    @JsonInclude()
    private Integer actualOnewayPal;
    @JsonInclude()
    private Integer processType;
    @JsonInclude()
    private String processTypeName;
    @JsonInclude()
    private String comments;
    @JsonInclude()
    private Integer typeErrorId;
    @JsonInclude()
    private String typeErrorName;
    @JsonInclude()
    private LocalTime startTime;
    @JsonInclude()
    private LocalTime finishTime;
    @JsonInclude()
    private String whoProcessing;

}

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
public class YardManDTO {

    private Long id;
    @JsonInclude()
    private LocalDate date;
    @JsonInclude()
    private Integer ramp;
    @JsonInclude()
    private String rampName;
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
    private String notificationNumber;
    @JsonInclude()
    private String bookingId;
    @JsonInclude()
    private Integer productType;
    @JsonInclude()
    private String productTypeName;
    @JsonInclude()
    private Integer processType;
    @JsonInclude()
    private String processTypeName;
    @JsonInclude()
    private Integer supplierType;
    @JsonInclude()
    private String supplierTypeName;
    @JsonInclude()
    private Integer palletExchange;
    @JsonInclude()
    private String palletExchangeName;
    @JsonInclude()
    private String comments;
    @JsonInclude()
    private Boolean isInTheYard;

}

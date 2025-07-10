package idc.inbound.response;

import idc.inbound.dto.unloading.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnloadingReportResponse {
    List<BramDTO> bram;
    List<PalletExchangeDTO> palletExchange;
    List<DeliveryTypeDTO> deliveryType;
    List<ProcessTypeDTO> processType;
    List<TypeErrorDTO> typeError;
    List<ProductTypeDTO> productType;
    List<RampDTO> ramp;
    List<StatusDTO> status;
    List<SupplierTypeDTO> supplierType;
}

package idc.inbound.request;

import com.opencsv.bean.CsvBindByName;

public record BookingAddRequest(
        String deliveryType,
        Integer qtyPalTotal,
        Integer qtyBoxTotal,
        Integer qtyItemTotal,
        String estimatedArrivalTime,
        String notificationNumber,
        String booking,
        String productType,
        String processType,
        String supplierType,
        String palletExchange
) {
}

package idc.inbound.dto.unloading;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class BookingCSV {
    @CsvBindByName(column = "Delivery type")
    String deliveryType;

    @CsvBindByName(column = "QTY PAL TOTAL")
    Integer qtyPalTotal;

    @CsvBindByName(column = "QTY BOXES TOTAL")
    Integer qtyBoxTotal;

    @CsvBindByName(column = "QTY ITEMS TOTAL")
    Integer qtyItemTotal;

    @CsvBindByName(column = "Planowana godzina przyjazdu")
    String estimatedArrivalTime;

    @CsvBindByName(column = "Numer awizacji")
    String notificationNumber;

    @CsvBindByName(column = "Booking ID")
    String booking;

    @CsvBindByName(column = "Product Type")
    String productType;

    @CsvBindByName(column = "Process Type")
    String processType;

    @CsvBindByName(column = "Supplier type")
    String supplierType;

    @CsvBindByName(column = "Pallet Exchange")
    String palletExchange;
}

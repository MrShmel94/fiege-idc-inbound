package idc.inbound.entity.unloading;

import idc.inbound.entity.vision.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "booking", schema = "unloading", uniqueConstraints = {
        @UniqueConstraint(name = "booking_notification_number_key", columnNames = {"notification_number"})
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ramp_id")
    private Ramp ramp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bram_id")
    private Bram bram;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id")
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_type_id", nullable = false)
    private DeliveryType deliveryType;

    @Column(name = "qty_pal")
    private Integer qtyPal;

    @Column(name = "qty_boxes")
    private Integer qtyBoxes;

    @Column(name = "qty_items")
    private Integer qtyItems;

    @NotNull
    @Column(name = "estimated_arrival_time", nullable = false)
    private LocalTime estimatedArrivalTime;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Size(max = 128)
    @NotNull
    @Column(name = "notification_number", nullable = false, length = 128)
    private String notificationNumber;

    @Size(max = 128)
    @NotNull
    @Column(name = "booking_id", nullable = false, length = 128)
    private String bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @Column(name = "actual_coli")
    private Integer actualColi;

    @Column(name = "actual_eu_pal")
    private Integer actualEuPal;

    @Column(name = "actual_eu_pal_defect")
    private Integer actualEuPalDefect;

    @Column(name = "actual_oneway_pal")
    private Integer actualOnewayPal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_type_id")
    private ProcessType processType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_type_id")
    private SupplierType supplierType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pallet_exchange_id", nullable = false)
    private PalletExchange palletExchange;

    @Column(name = "comments", length = Integer.MAX_VALUE)
    private String comments;

    @ColumnDefault("false")
    @Column(name = "is_behind_the_gate")
    private Boolean isBehindTheGate;

    @ColumnDefault("false")
    @Column(name = "is_in_the_yard")
    private Boolean isInTheYard;

    @ColumnDefault("false")
    @Column(name = "is_at_the_yard")
    private Boolean isAtTheYard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_error_id")
    private TypeError typeError;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "finish_time")
    private LocalTime finishTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "who_processing")
    private User whoProcessing;
}
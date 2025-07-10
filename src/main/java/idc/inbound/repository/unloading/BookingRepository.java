package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.entity.unloading.Booking;
import idc.inbound.entity.unloading.Bram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT new idc.inbound.dto.unloading.BookingDTO(
            b.id, b.date,
            r.id, r.name,
            br.id, br.name,
            s.id, s.name,
            dt.id, dt.name,
            b.qtyPal, b.qtyBoxes, b.qtyItems,
            b.estimatedArrivalTime, b.arrivalTime,
            b.notificationNumber, b.bookingId,
            pt.id, pt.name,
            b.actualColi, b.actualEuPal, b.actualEuPalDefect, b.actualOnewayPal,
            prt.id, prt.name,
            st.id, st.name,
            pe.id, pe.name,
            b.comments, b.isBehindTheGate, b.isInTheYard, b.isAtTheYard, te.id,
            te.name, b.startTime, b.finishTime, CONCAT(
                                                  COALESCE(cu.name, ''),
                                                  ' ',
                                                  COALESCE(cu.secondName, '')
                                                )
        )
        FROM Booking b
        LEFT JOIN b.ramp r
        LEFT JOIN b.bram br
        LEFT JOIN b.status s
        LEFT JOIN b.deliveryType dt
        LEFT JOIN b.productType pt
        LEFT JOIN b.processType prt
        LEFT JOIN b.supplierType st
        LEFT JOIN b.palletExchange pe
        LEFT JOIN b.typeError te
        LEFT JOIN b.whoProcessing user
        LEFT JOIN user.createdByUser cu
        WHERE b.status.name IN (:statusName)
    """)
    List<BookingDTO> getAllBookingByStatusName(@Param("statusName") List<String> statusName);

    @Query("""
        SELECT new idc.inbound.dto.unloading.BookingDTO(
            b.id, b.date,
            r.id, r.name,
            br.id, br.name,
            s.id, s.name,
            dt.id, dt.name,
            b.qtyPal, b.qtyBoxes, b.qtyItems,
            b.estimatedArrivalTime, b.arrivalTime,
            b.notificationNumber, b.bookingId,
            pt.id, pt.name,
            b.actualColi, b.actualEuPal, b.actualEuPalDefect, b.actualOnewayPal,
            prt.id, prt.name,
            st.id, st.name,
            pe.id, pe.name,
            b.comments, b.isBehindTheGate, b.isInTheYard, b.isAtTheYard, te.id,
            te.name, b.startTime, b.finishTime, CONCAT(
                                                  COALESCE(cu.name, ''),
                                                  ' ',
                                                  COALESCE(cu.secondName, '')
                                                )
        )
        FROM Booking b
        LEFT JOIN b.ramp r
        LEFT JOIN b.bram br
        LEFT JOIN b.status s
        LEFT JOIN b.deliveryType dt
        LEFT JOIN b.productType pt
        LEFT JOIN b.processType prt
        LEFT JOIN b.supplierType st
        LEFT JOIN b.palletExchange pe
        LEFT JOIN b.typeError te
        LEFT JOIN b.whoProcessing user
        LEFT JOIN user.createdByUser cu
        WHERE b.date = :date
    """)
    List<BookingDTO> getAllBookingChoiceDate(@Param("date") LocalDate date);

    @Query("""
        SELECT new idc.inbound.dto.unloading.BookingDTO(
            b.id, b.date,
            r.id, r.name,
            br.id, br.name,
            s.id, s.name,
            dt.id, dt.name,
            b.qtyPal, b.qtyBoxes, b.qtyItems,
            b.estimatedArrivalTime, b.arrivalTime,
            b.notificationNumber, b.bookingId,
            pt.id, pt.name,
            b.actualColi, b.actualEuPal, b.actualEuPalDefect, b.actualOnewayPal,
            prt.id, prt.name,
            st.id, st.name,
            pe.id, pe.name,
            b.comments, b.isBehindTheGate, b.isInTheYard, b.isAtTheYard, te.id,
            te.name, b.startTime, b.finishTime, CONCAT(
                                                  COALESCE(cu.name, ''),
                                                  ' ',
                                                  COALESCE(cu.secondName, '')
                                                )
        )
        FROM Booking b
        LEFT JOIN b.ramp r
        LEFT JOIN b.bram br
        LEFT JOIN b.status s
        LEFT JOIN b.deliveryType dt
        LEFT JOIN b.productType pt
        LEFT JOIN b.processType prt
        LEFT JOIN b.supplierType st
        LEFT JOIN b.palletExchange pe
        LEFT JOIN b.typeError te
        LEFT JOIN b.whoProcessing user
        LEFT JOIN user.createdByUser cu
        WHERE b.id = :id
    """)
    Optional<BookingDTO> getBookingDTOById(@Param("id") Long id);

    @Query("""
        SELECT new idc.inbound.dto.unloading.BookingDTO(
            b.id, b.date,
            r.id, r.name,
            br.id, br.name,
            s.id, s.name,
            dt.id, dt.name,
            b.qtyPal, b.qtyBoxes, b.qtyItems,
            b.estimatedArrivalTime, b.arrivalTime,
            b.notificationNumber, b.bookingId,
            pt.id, pt.name,
            b.actualColi, b.actualEuPal, b.actualEuPalDefect, b.actualOnewayPal,
            prt.id, prt.name,
            st.id, st.name,
            pe.id, pe.name,
            b.comments, b.isBehindTheGate, b.isInTheYard, b.isAtTheYard, te.id,
            te.name, b.startTime, b.finishTime, CONCAT(
                                                  COALESCE(cu.name, ''),
                                                  ' ',
                                                  COALESCE(cu.secondName, '')
                                                )
        )
        FROM Booking b
        LEFT JOIN b.ramp r
        LEFT JOIN b.bram br
        LEFT JOIN b.status s
        LEFT JOIN b.deliveryType dt
        LEFT JOIN b.productType pt
        LEFT JOIN b.processType prt
        LEFT JOIN b.supplierType st
        LEFT JOIN b.palletExchange pe
        LEFT JOIN b.typeError te
        LEFT JOIN b.whoProcessing user
        LEFT JOIN user.createdByUser cu
        WHERE b.id IN :ids
    """)
    List<BookingDTO> getBookingDTOByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Booking b SET b.date = :date WHERE b.id = :id")
    void updateDate(@Param("id") Long id, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Booking b SET b.ramp.id = :rampId WHERE b.id = :id")
    void updateRamp(@Param("id") Long id, @Param("rampId") Integer rampId);

    @Modifying
    @Query("UPDATE Booking b SET b.bram.id = :bramId WHERE b.id = :id")
    void updateBram(@Param("id") Long id, @Param("bramId") Integer bramId);

    @Modifying
    @Query("UPDATE Booking b SET b.status.id = :statusId WHERE b.id = :id")
    void updateStatus(@Param("id") Long id, @Param("statusId") Integer statusId);

    @Modifying
    @Query("UPDATE Booking b SET b.deliveryType.id = :deliveryTypeId WHERE b.id = :id")
    void updateDeliveryType(@Param("id") Long id, @Param("deliveryTypeId") Integer deliveryTypeId);

    @Modifying
    @Query("UPDATE Booking b SET b.qtyPal = :qtyPal WHERE b.id = :id")
    void updateQtyPal(@Param("id") Long id, @Param("qtyPal") Integer qtyPal);

    @Modifying
    @Query("UPDATE Booking b SET b.qtyBoxes = :qtyBoxes WHERE b.id = :id")
    void updateQtyBoxes(@Param("id") Long id, @Param("qtyBoxes") Integer qtyBoxes);

    @Modifying
    @Query("UPDATE Booking b SET b.qtyItems = :qtyItems WHERE b.id = :id")
    void updateQtyItems(@Param("id") Long id, @Param("qtyItems") Integer qtyItems);

    @Modifying
    @Query("UPDATE Booking b SET b.estimatedArrivalTime = :estimatedArrivalTime WHERE b.id = :id")
    void updateEstimatedArrivalTime(@Param("id") Long id, @Param("estimatedArrivalTime") LocalTime estimatedArrivalTime);

    @Modifying
    @Query("UPDATE Booking b SET b.arrivalTime = :arrivalTime WHERE b.id = :id")
    void updateArrivalTime(@Param("id") Long id, @Param("arrivalTime") LocalTime estimatedArrivalTime);

    @Modifying
    @Query("UPDATE Booking b SET b.notificationNumber = :notificationNumber WHERE b.id = :id")
    void updateNotificationNumber(@Param("id") Long id, @Param("notificationNumber") String notificationNumber);

    @Modifying
    @Query("UPDATE Booking b SET b.bookingId = :bookingId WHERE b.id = :id")
    void updateBookingId(@Param("id") Long id, @Param("bookingId") String bookingId);

    @Modifying
    @Query("UPDATE Booking b SET b.productType.id = :productTypeId WHERE b.id = :id")
    void updateProductType(@Param("id") Long id, @Param("productTypeId") Integer productTypeId);

    @Modifying
    @Query("UPDATE Booking b SET b.actualColi = :actualColi WHERE b.id = :id")
    void updateActualColi(@Param("id") Long id, @Param("actualColi") Integer actualColi);

    @Modifying
    @Query("UPDATE Booking b SET b.actualEuPal = :actualEuPal WHERE b.id = :id")
    void updateActualEuPal(@Param("id") Long id, @Param("actualEuPal") Integer actualEuPal);

    @Modifying
    @Query("UPDATE Booking b SET b.actualEuPalDefect = :actualEuPalDefect WHERE b.id = :id")
    void updateActualEuPalDefect(@Param("id") Long id, @Param("actualEuPalDefect") Integer actualEuPalDefect);

    @Modifying
    @Query("UPDATE Booking b SET b.actualOnewayPal = :actualOnewayPal WHERE b.id = :id")
    void updateActualOnewayPal(@Param("id") Long id, @Param("actualOnewayPal") Integer actualOnewayPal);

    @Modifying
    @Query("UPDATE Booking b SET b.processType.id = :processTypeId WHERE b.id = :id")
    void updateProcessType(@Param("id") Long id, @Param("processTypeId") Integer processTypeId);

    @Modifying
    @Query("UPDATE Booking b SET b.supplierType.id = :supplierTypeId WHERE b.id = :id")
    void updateSupplierType(@Param("id") Long id, @Param("supplierTypeId") Integer supplierTypeId);

    @Modifying
    @Query("UPDATE Booking b SET b.palletExchange.id = :palletExchange WHERE b.id = :id")
    void updatePalletExchange(@Param("id") Long id, @Param("palletExchange") Integer palletExchange);

    @Modifying
    @Query("UPDATE Booking b SET b.comments = :comments WHERE b.id = :id")
    void updateComments(@Param("id") Long id, @Param("comments") String comments);

    @Modifying
    @Query("UPDATE Booking b SET b.isBehindTheGate = :isBehindTheGate, b.isInTheYard = false, b.isAtTheYard = false WHERE b.id = :id")
    void updateIsBehindTheGate(@Param("id") Long id, @Param("isBehindTheGate") Boolean isBehindTheGate);

    @Modifying
    @Query("UPDATE Booking b SET b.isInTheYard = :isInTheYard, b.isBehindTheGate = false, b.isAtTheYard = false WHERE b.id = :id")
    void updateIsInTheYard(@Param("id") Long id, @Param("isInTheYard") Boolean isInTheYard);

    @Modifying
    @Query("UPDATE Booking b SET b.isAtTheYard = :isAtTheYard, b.isBehindTheGate = false, b.isInTheYard = false WHERE b.id = :id")
    void updateIsAtTheYard(@Param("id") Long id, @Param("isAtTheYard") Boolean isAtTheYard);

    @Modifying
    @Query("UPDATE Booking b SET b.typeError.id = :typeErrorId WHERE b.id = :id")
    void updateTypeError(@Param("id") Long id, @Param("typeError") Integer typeErrorId);

    @Modifying
    @Query("UPDATE Booking b SET b.startTime = :startTime WHERE b.id = :id")
    void updateStartTime(@Param("id") Long id, @Param("startTime") LocalTime startTime);

    @Modifying
    @Query("UPDATE Booking b SET b.finishTime = :finishTime WHERE b.id = :id")
    void updateFinishTime(@Param("id") Long id, @Param("finishTime") LocalTime finishTime);

    @Modifying
    @Query("UPDATE Booking b SET b.whoProcessing.id = :userId WHERE b.id = :id")
    void updateWhoProcessing(@Param("id") Long id, @Param("userId") Long userId);
}

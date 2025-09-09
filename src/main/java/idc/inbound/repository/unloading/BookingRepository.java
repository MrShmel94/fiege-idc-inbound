package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.dto.unloading.YardManDTO;
import idc.inbound.entity.unloading.*;
import idc.inbound.entity.vision.User;
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
                                                ), cu.id
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
                                                ), cu.id
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
        WHERE b.date = :currentDate
        AND b.status.name != 'Zaawizowane'
    """)
    List<BookingDTO> getBookingByForkLift(@Param("currentDate") LocalDate currentDate);

    @Query("""
        SELECT new idc.inbound.dto.unloading.YardManDTO(
            b.id, b.date,
            rm.id, rm.name,
            dt.id, dt.name,
            b.qtyPal, b.qtyBoxes, b.qtyItems,
            b.estimatedArrivalTime,
            b.notificationNumber, b.bookingId,
            pt.id, pt.name,
            prt.id, prt.name,
            st.id, st.name,
            pe.id, pe.name,
            b.comments, b.isInTheYard
        )
        FROM Booking b
        LEFT JOIN b.ramp rm
        LEFT JOIN b.deliveryType dt
        LEFT JOIN b.productType pt
        LEFT JOIN b.processType prt
        LEFT JOIN b.supplierType st
        LEFT JOIN b.palletExchange pe
        WHERE b.date = :today
        AND b.status.name = 'Zaawizowane'
    """)
    List<YardManDTO> getAllBookingByYardMan(@Param("today") LocalDate today);

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
                                                ), cu.id
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
                                                ), cu.id
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
                                                ), cu.id
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

    @Query("""
           SELECT b.status.name FROM Booking b WHERE b.id = :id
           """)
    String getStatusName(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Booking b SET b.date = :date WHERE b.id = :id")
    void updateDate(@Param("id") Long id, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Booking b SET b.ramp = :ramp, b.status = :status, b.arrivalTime = :time WHERE b.id = :id")
    void updateRamp(@Param("id") Long id, @Param("ramp") Ramp rampId, @Param("status") Status status, @Param("time") LocalTime time);

    @Modifying
    @Query("UPDATE Booking b SET b.bram = :bram WHERE b.id = :id")
    void updateBram(@Param("id") Long id, @Param("bram") Bram bram);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") Status status);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id IN (:ids)")
    void updateStatuses(@Param("ids") List<Long> ids, @Param("status") Status status);

    @Modifying
    @Query("UPDATE Booking b SET b.status = :status, b.whoProcessing = null WHERE b.id IN (:ids)")
    void updateStatusesPause(@Param("ids") List<Long> ids, @Param("status") Status status);

    @Modifying
    @Query(value = """
    UPDATE Booking b
    SET b.status = :status,
        b.whoProcessing = :user,
        b.startTime = COALESCE(:startTime, b.startTime)
    WHERE b.id IN (:ids) AND b.ramp IS NOT NULL
    """)
    int updateStartOrResume(@Param("ids") List<Long> ids,
                             @Param("status") Status status,
                             @Param("user") User user,
                             @Param("isStart") LocalTime isStart,
                             @Param("startTime") LocalTime startTime);

    @Modifying
    @Query("UPDATE Booking b SET b.deliveryType = :deliveryType WHERE b.id = :id")
    void updateDeliveryType(@Param("id") Long id, @Param("deliveryType") DeliveryType deliveryType);

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
    @Query("UPDATE Booking b SET b.productType = :productType WHERE b.id = :id")
    void updateProductType(@Param("id") Long id, @Param("productType") ProductType productType);

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
    @Query("UPDATE Booking b SET b.processType = :processType WHERE b.id = :id")
    void updateProcessType(@Param("id") Long id, @Param("processType") ProcessType processType);

    @Modifying
    @Query("UPDATE Booking b SET b.supplierType = :supplierType WHERE b.id = :id")
    void updateSupplierType(@Param("id") Long id, @Param("supplierType") SupplierType supplierType);

    @Modifying
    @Query("UPDATE Booking b SET b.palletExchange = :palletExchange WHERE b.id = :id")
    void updatePalletExchange(@Param("id") Long id, @Param("palletExchange") PalletExchange palletExchange);

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
    @Query("UPDATE Booking b SET b.typeError = :typeError WHERE b.id = :id")
    void updateTypeError(@Param("id") Long id, @Param("typeError") TypeError typeError);

    @Modifying
    @Query("UPDATE Booking b SET b.startTime = :startTime WHERE b.id IN (:ids)")
    void updateStartTime(@Param("ids") List<Long> ids, @Param("startTime") LocalTime startTime);

    @Modifying
    @Query("UPDATE Booking b SET b.finishTime = :finishTime WHERE b.id = :id")
    void updateFinishTime(@Param("id") Long id, @Param("finishTime") LocalTime finishTime);

    @Modifying
    @Query("UPDATE Booking b SET b.whoProcessing = :user WHERE b.id = :id")
    void updateWhoProcessing(@Param("id") Long id, @Param("user") User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Booking b
            SET
              b.status    = :status,
              b.typeError = :typeError,
              b.comments  = COALESCE(NULLIF(:comment, ''), b.comments)
            WHERE b.id = :id
            """)
    void updateStatusTypeErrorComment(@Param("id") Long id,
                                      @Param("status") Status status,
                                      @Param("typeError") TypeError typeError,
                                      @Param("comment") String comment);

    @Query(value = """
        WITH updated_bookings AS (
          UPDATE unloading.booking b
          SET finish_time = :finishTime
          WHERE b.id IN (:ids)
          RETURNING b.ramp_id
        ),
        single_ramp AS (
          SELECT ramp_id
          FROM updated_bookings
          WHERE ramp_id IS NOT NULL
          LIMIT 1
        ),
        sum_bookings_pal AS (
          SELECT
            COALESCE(SUM(
                COALESCE(b.actual_oneway_pal, 0)
              + COALESCE(b.actual_eu_pal, 0)
              + COALESCE(b.actual_eu_pal_defect, 0)
            ), 0) AS sum_pal
          FROM unloading.booking b
          WHERE b.id IN (:ids)
        )
        UPDATE unloading.ramp r
        SET status        = :rampStatus,
            actual_buffer = sp.sum_pal
        FROM single_ramp sr
        CROSS JOIN sum_bookings_pal sp
        WHERE r.id = sr.ramp_id
        RETURNING r.id, r.status, r.name, r.max_buffer, r.actual_buffer
        """, nativeQuery = true)
    Optional<Object[]> endAndUpdateRampsReturnId(
            @Param("ids") List<Long> ids,
            @Param("finishTime") LocalTime finishTime,
            @Param("rampStatus") String rampStatus
    );
}

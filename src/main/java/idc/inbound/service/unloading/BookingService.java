package idc.inbound.service.unloading;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.entity.unloading.Booking;
import idc.inbound.request.BookingFieldUpdateRequest;
import org.springframework.data.repository.query.Param;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingService {

    List<BookingDTO> getAllBookingsChoiceDate(LocalDate date);
    BookingDTO getBookingById(Long id);
    List<BookingDTO> getBookingsByIds(List<Long> ids);

    List<BookingDTO> getBookingsByStatusName(List<String> statusName);

    void updateField(BookingFieldUpdateRequest update, Principal principal);
    void updateFieldPerForkLift(BookingFieldUpdateRequest update, Principal principal);
    void deleteBooking(Long id, Principal principal, LocalDate date);


    List<Booking> saveAllBookings(List<Booking> bookings);
    void save(Booking booking);

    void updateDate(Long id, LocalDate date);
    void updateRamp(Long id, Integer rampId);
    void updateBram(Long id, Integer bramId);
    void updateStatus(Long id, Integer statusId);
    void updateDeliveryType(Long id, Integer deliveryTypeId);
    void updateQtyPal(Long id, Integer qtyPal);
    void updateQtyBoxes(Long id, Integer qtyBoxes);
    void updateQtyItems(Long id, Integer qtyItems);
    void updateEstimatedArrivalTime(Long id, LocalTime estimatedArrivalTime);
    void updateArrivalTime(Long id, LocalTime estimatedArrivalTime);
    void updateNotificationNumber(Long id, String notificationNumber);
    void updateBookingId(Long id, String updateBookingId);
    void updateProductType(Long id, Integer productTypeId);
    void updateActualColi(Long id, Integer actualColi);
    void updateActualEuPal(Long id, Integer actualEuPal);
    void updateActualEuPalDefect(Long id, Integer actualEuPalDefect);
    void updateActualOnewayPal(Long id, Integer actualOnewayPal);
    void updateProcessType(Long id, Integer updateProcessTypeId);
    void updateSupplierType(Long id, Integer supplierTypeId);
    void updatePalletExchange(Long id, Integer palletExchange);
    void updateComments(Long id, String comments);
    void updateIsBehindTheGate(Long id, Boolean isBehindTheGate);
    void updateIsInTheYard(Long id, Boolean isInTheYard);
    void updateIsAtTheYard(Long id, Boolean isAtTheYard);

    void updateTypeError(Long id, Integer typeErrorId);
    void updateStartTime(Long id, LocalTime startTime);
    void updateFinishTime(Long id, LocalTime finishTime);
    void updateWhoProcessing(Long id, Long userId);
}

package idc.inbound.serviceImpl.unloading;

import idc.inbound.configuration.BookingCreatedEvent;
import idc.inbound.configuration.BookingDeleteEvent;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.entity.unloading.Booking;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.BookingRepository;
import idc.inbound.request.BookingFieldUpdateRequest;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.secure.aspect.AccessControl;
import idc.inbound.service.unloading.BookingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RedisCacheService redisCacheService;
    private final SecurityUtils securityUtils;

    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BookingDTO> getBookingsByStatusName(List<String> statusName) {
        return bookingRepository.getAllBookingByStatusName(statusName);
    }

    @Override
    @Transactional
    @AccessControl(
            minWeight = 998
    )
    public void deleteBooking(Long id, Principal principal, LocalDate date){

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();
        bookingRepository.deleteById(id);

        eventPublisher.publishEvent(new BookingDeleteEvent(id, "RECORD_DELETE", date));

        SecurityContextHolder.clearContext();
    }


    @Override
    @Transactional
    public void updateField(BookingFieldUpdateRequest update, Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();

        Long id = update.recordId();
        String field = update.field();
        Object value = update.value();
        LocalDate date = update.date();

        switch (field) {
            case "date" -> bookingRepository.updateDate(id, LocalDate.parse(value.toString()));
            case "ramp" -> bookingRepository.updateRamp(id, Integer.valueOf(value.toString()));
            case "bram" -> bookingRepository.updateBram(id, Integer.valueOf(value.toString()));
            case "status" -> bookingRepository.updateStatus(id, Integer.valueOf(value.toString()));
            case "deliveryType" -> bookingRepository.updateDeliveryType(id, Integer.valueOf(value.toString()));
            case "qtyPal" -> bookingRepository.updateQtyPal(id, Integer.valueOf(value.toString()));
            case "qtyBox" -> bookingRepository.updateQtyBoxes(id, Integer.valueOf(value.toString()));
            case "qtyItems" -> bookingRepository.updateQtyItems(id, Integer.valueOf(value.toString()));
            case "estimatedArrivalTime" -> bookingRepository.updateEstimatedArrivalTime(id, LocalTime.parse(value.toString()));
            case "arrivalTime" -> bookingRepository.updateArrivalTime(id, LocalTime.parse(value.toString()));
            case "notificationNumber" -> bookingRepository.updateNotificationNumber(id, value.toString());
            case "bookingId" -> bookingRepository.updateBookingId(id, value.toString());
            case "productType" -> bookingRepository.updateProductType(id, Integer.valueOf(value.toString()));
            case "actualColi" -> bookingRepository.updateActualColi(id, Integer.valueOf(value.toString()));
            case "actualEuPal" -> bookingRepository.updateActualEuPal(id, Integer.valueOf(value.toString()));
            case "actualEuPalDefect" -> bookingRepository.updateActualEuPalDefect(id, Integer.valueOf(value.toString()));
            case "actualOnewayPal" -> bookingRepository.updateActualOnewayPal(id, Integer.valueOf(value.toString()));
            case "processType" -> bookingRepository.updateProcessType(id, Integer.valueOf(value.toString())); //?
            case "supplierType" -> bookingRepository.updateSupplierType(id, Integer.valueOf(value.toString())); //?
            case "palletExchange" -> bookingRepository.updatePalletExchange(id, Integer.valueOf(value.toString()));
            case "comments" -> bookingRepository.updateComments(id, value.toString());
            case "isBehindTheGate" -> bookingRepository.updateIsBehindTheGate(id, Boolean.valueOf(value.toString()));
            case "isInTheYard" -> bookingRepository.updateIsInTheYard(id, Boolean.valueOf(value.toString()));
            case "isAtTheYard" -> bookingRepository.updateIsAtTheYard(id, Boolean.valueOf(value.toString()));
            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        }

        SecurityContextHolder.clearContext();

        eventPublisher.publishEvent(new BookingCreatedEvent(id, "RECORD_UPDATE", date));
    }

    @Override
    @Transactional
    public void updateFieldPerForkLift(BookingFieldUpdateRequest update, Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();

        Long id = update.recordId();
        String field = update.field();
        Object value = update.value();
        LocalDate date = update.date();

        switch (field) {
            case "status" -> bookingRepository.updateStatus(id, Integer.valueOf(value.toString()));

            case "typeError" -> bookingRepository.updateTypeError(id, Integer.valueOf(value.toString()));
            case "startTime" -> bookingRepository.updateStartTime(id, LocalTime.parse(value.toString()));
            case "finishTime" -> bookingRepository.updateFinishTime(id, LocalTime.parse(value.toString()));
            case "whoProcessing" -> bookingRepository.updateWhoProcessing(id, Long.valueOf(value.toString()));
            case "actualColi" -> bookingRepository.updateActualColi(id, Integer.valueOf(value.toString()));
            case "actualEuPal" -> bookingRepository.updateActualEuPal(id, Integer.valueOf(value.toString()));
            case "actualEuPalDefect" -> bookingRepository.updateActualEuPalDefect(id, Integer.valueOf(value.toString()));
            case "actualOnewayPal" -> bookingRepository.updateActualOnewayPal(id, Integer.valueOf(value.toString()));

            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        }

        SecurityContextHolder.clearContext();

        eventPublisher.publishEvent(new BookingCreatedEvent(id, "RECORD_UPDATE", date));
    }

    @Override
    public List<BookingDTO> getAllBookingsChoiceDate(LocalDate date) {
        return bookingRepository.getAllBookingChoiceDate(date);
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.getBookingDTOById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Override
    public List<BookingDTO> getBookingsByIds(List<Long> ids) {
        return bookingRepository.getBookingDTOByIds(ids);
    }

    @Override
    public List<Booking> saveAllBookings(List<Booking> bookings) {
        return bookingRepository.saveAll(bookings);
    }

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void updateDate(Long id, LocalDate date) {
        setCurrentUserId();
        bookingRepository.updateDate(id, date);
    }

    @Override
    @Transactional
    public void updateRamp(Long id, Integer rampId) {
        setCurrentUserId();
        bookingRepository.updateRamp(id, rampId);
    }

    @Override
    @Transactional
    public void updateBram(Long id, Integer bramId) {
        setCurrentUserId();
        bookingRepository.updateBram(id, bramId);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer statusId) {
        setCurrentUserId();
        bookingRepository.updateStatus(id, statusId);
    }

    @Override
    @Transactional
    public void updateDeliveryType(Long id, Integer deliveryTypeId) {
        setCurrentUserId();
        bookingRepository.updateDeliveryType(id, deliveryTypeId);
    }

    @Override
    @Transactional
    public void updateQtyPal(Long id, Integer qtyPal) {
        setCurrentUserId();
        bookingRepository.updateQtyPal(id, qtyPal);
    }

    @Override
    @Transactional
    public void updateQtyBoxes(Long id, Integer qtyBoxes) {
        setCurrentUserId();
        bookingRepository.updateQtyBoxes(id, qtyBoxes);
    }

    @Override
    @Transactional
    public void updateQtyItems(Long id, Integer qtyItems) {
        setCurrentUserId();
        bookingRepository.updateQtyItems(id, qtyItems);
    }

    @Override
    @Transactional
    public void updateEstimatedArrivalTime(Long id, LocalTime estimatedArrivalTime) {
        setCurrentUserId();
        bookingRepository.updateEstimatedArrivalTime(id, estimatedArrivalTime);
    }

    @Override
    @Transactional
    public void updateArrivalTime(Long id, LocalTime estimatedArrivalTime) {
        setCurrentUserId();
        bookingRepository.updateArrivalTime(id, estimatedArrivalTime);
    }

    @Override
    @Transactional
    public void updateNotificationNumber(Long id, String notificationNumber) {
        setCurrentUserId();
        bookingRepository.updateNotificationNumber(id, notificationNumber);
    }

    @Override
    @Transactional
    public void updateBookingId(Long id, String updateBookingId) {
        setCurrentUserId();
        bookingRepository.updateBookingId(id, updateBookingId);
    }

    @Override
    @Transactional
    public void updateProductType(Long id, Integer productTypeId) {
        setCurrentUserId();
        bookingRepository.updateProductType(id, productTypeId);
    }

    @Override
    @Transactional
    public void updateActualColi(Long id, Integer actualColi) {
        setCurrentUserId();
        bookingRepository.updateActualColi(id, actualColi);
    }

    @Override
    @Transactional
    public void updateActualEuPal(Long id, Integer actualEuPal) {
        setCurrentUserId();
        bookingRepository.updateActualEuPal(id, actualEuPal);
    }

    @Override
    @Transactional
    public void updateActualEuPalDefect(Long id, Integer actualEuPalDefect) {
        setCurrentUserId();
        bookingRepository.updateActualEuPalDefect(id, actualEuPalDefect);
    }

    @Override
    @Transactional
    public void updateActualOnewayPal(Long id, Integer actualOnewayPal) {
        setCurrentUserId();
        bookingRepository.updateActualOnewayPal(id, actualOnewayPal);
    }

    @Override
    @Transactional
    public void updateProcessType(Long id, Integer updateProcessTypeId) {
        setCurrentUserId();
        bookingRepository.updateProcessType(id, updateProcessTypeId);
    }

    @Override
    @Transactional
    public void updateSupplierType(Long id, Integer supplierTypeId) {
        setCurrentUserId();
        bookingRepository.updateSupplierType(id, supplierTypeId);
    }

    @Override
    @Transactional
    public void updatePalletExchange(Long id, Integer palletExchange) {
        setCurrentUserId();
        bookingRepository.updatePalletExchange(id, palletExchange);
    }

    @Override
    @Transactional
    public void updateComments(Long id, String comments) {
        setCurrentUserId();
        bookingRepository.updateComments(id, comments);
    }

    @Override
    @Transactional
    public void updateIsBehindTheGate(Long id, Boolean isBehindTheGate) {
        setCurrentUserId();
        bookingRepository.updateIsBehindTheGate(id, isBehindTheGate);
    }

    @Override
    @Transactional
    public void updateIsInTheYard(Long id, Boolean isInTheYard) {
        setCurrentUserId();
        bookingRepository.updateIsInTheYard(id, isInTheYard);
    }

    @Override
    @Transactional
    public void updateIsAtTheYard(Long id, Boolean isAtTheYard) {
        setCurrentUserId();
        bookingRepository.updateIsAtTheYard(id, isAtTheYard);
    }

    @Override
    @Transactional
    public void updateTypeError(Long id, Integer typeErrorId) {
        setCurrentUserId();
        bookingRepository.updateTypeError(id, typeErrorId);
    }

    @Override
    @Transactional
    public void updateStartTime(Long id, LocalTime startTime) {
        setCurrentUserId();
        bookingRepository.updateStartTime(id, startTime);
    }

    @Override
    @Transactional
    public void updateFinishTime(Long id, LocalTime finishTime) {
        setCurrentUserId();
        bookingRepository.updateFinishTime(id, finishTime);
    }

    @Override
    @Transactional
    public void updateWhoProcessing(Long id, Long userId) {
        setCurrentUserId();
        bookingRepository.updateWhoProcessing(id, userId);
    }

    private void setCurrentUserId() {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userDetails.id()))
                .getSingleResult();
    }
}

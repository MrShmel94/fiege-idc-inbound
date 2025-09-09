package idc.inbound.serviceImpl.unloading;

import idc.inbound.configuration.BookingCreatedEvent;
import idc.inbound.configuration.BookingDeleteEvent;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.BookingCacheUpdateEvent;
import idc.inbound.dto.RedisBookingDTO;
import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.dto.unloading.RampDTO;
import idc.inbound.dto.unloading.StatusDTO;
import idc.inbound.dto.unloading.YardManDTO;
import idc.inbound.entity.unloading.*;
import idc.inbound.entity.vision.User;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.BookingRepository;
import idc.inbound.request.BookingFieldUpdateRequest;
import idc.inbound.request.ForkliftControlUpdate;
import idc.inbound.request.YardManControlUpdate;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.secure.aspect.AccessControl;
import idc.inbound.service.unloading.BookingService;
import idc.inbound.service.unloading.RampService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RampService rampService;
    private final SecurityUtils securityUtils;
    private final StatusServiceImpl statusService;

    private final RedisCacheService redisCacheService;

    private final ApplicationEventPublisher eventPublisher;

    private final Set<String> KEY_STATUS_SET = Set.of("W trakcie rozładunku", "Pauza");
    private final String KEY_STATUS_RAMP = "ENABLED";
    private final String STANDARD_STATUS_NAME = "Zaawizowane";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BookingDTO> getBookingByForkLift() {
        return bookingRepository.getBookingByForkLift(LocalDate.now());
    }

    @Override
    public List<YardManDTO> getBookingsByYardMan() {
        return bookingRepository.getAllBookingByYardMan(LocalDate.now());
    }

    @Override
    @Transactional
    @AccessControl(
            minWeight = 100
    )
    public void deleteBooking(Long id, Principal principal, LocalDate date){

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();
        bookingRepository.deleteById(id);

        eventPublisher.publishEvent(new BookingDeleteEvent(id, "RECORD_DELETE", date, List.of("/topic/unloading-report/" + date, "/topic/forklift-socket", "/topic/yard-man")));

        SecurityContextHolder.clearContext();
    }


    @Override
    @Transactional
    @AccessControl(
            minWeight = 90
    )
    public void updateField(BookingFieldUpdateRequest update, Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();

        Long id = update.recordId();
        String field = update.field();
        String value = update.value();
        LocalDate date = update.date();

        String currentStatus = bookingRepository.getStatusName(id);

        if (KEY_STATUS_SET.contains(currentStatus)) {
            throw new IllegalStateException("Booking is already started. Cannot modify.");
        }

        List<StatusDTO> statuses = statusService.getAllDTO();

        switch (field) {
            case "date" -> bookingRepository.updateDate(id, LocalDate.parse(value));
            case "ramp" -> {
                Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Gotowe do rozładunku")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();

                String currentStatusRamp = rampService.getStatusNameById(Integer.parseInt(value));

                if (!currentStatusRamp.equals(KEY_STATUS_RAMP)) {
                    throw new IllegalStateException("Ramp don't free. You can't put this ramp");
                }

                bookingRepository.updateRamp(id, entityManager.getReference(Ramp.class, Integer.parseInt(value)), entityManager.getReference(Status.class, statusId), LocalTime.now());
                RampDTO ramp = rampService.setStatusToBramId(Integer.parseInt(value), StatusBramAndRamp.OCCUPIED);
                rampService.notifyUnloadingReportConfigUpdate(ramp);
            }
            case "bram" -> bookingRepository.updateBram(id, entityManager.getReference(Bram.class, Integer.valueOf(value)));
            case "status" -> bookingRepository.updateStatus(id, entityManager.getReference(Status.class, Integer.valueOf(value)));
            case "deliveryType" -> bookingRepository.updateDeliveryType(id, entityManager.getReference(DeliveryType.class, Integer.valueOf(value)));
            case "qtyPal" -> bookingRepository.updateQtyPal(id, Integer.valueOf(value));
            case "qtyBox" -> bookingRepository.updateQtyBoxes(id, Integer.valueOf(value));
            case "qtyItems" -> bookingRepository.updateQtyItems(id, Integer.valueOf(value));
            case "estimatedArrivalTime" -> bookingRepository.updateEstimatedArrivalTime(id, LocalTime.parse(value));
            case "arrivalTime" -> bookingRepository.updateArrivalTime(id, LocalTime.parse(value));
            case "notificationNumber" -> bookingRepository.updateNotificationNumber(id, value);
            case "bookingId" -> bookingRepository.updateBookingId(id, value);
            case "productType" -> bookingRepository.updateProductType(id, entityManager.getReference(ProductType.class, Integer.valueOf(value)));
            case "actualColi" -> bookingRepository.updateActualColi(id, Integer.valueOf(value));
            case "actualEuPal" -> bookingRepository.updateActualEuPal(id, Integer.valueOf(value));
            case "actualEuPalDefect" -> bookingRepository.updateActualEuPalDefect(id, Integer.valueOf(value));
            case "actualOnewayPal" -> bookingRepository.updateActualOnewayPal(id, Integer.valueOf(value));
            case "processType" -> bookingRepository.updateProcessType(id, entityManager.getReference(ProcessType.class, Integer.valueOf(value)));
            case "supplierType" -> bookingRepository.updateSupplierType(id, entityManager.getReference(SupplierType.class, Integer.valueOf(value)));
            case "palletExchange" -> bookingRepository.updatePalletExchange(id, entityManager.getReference(PalletExchange.class, Integer.valueOf(value)));
            case "comments" -> bookingRepository.updateComments(id, value);
            case "isBehindTheGate" -> bookingRepository.updateIsBehindTheGate(id, Boolean.valueOf(value));
            case "isInTheYard" -> bookingRepository.updateIsInTheYard(id, Boolean.valueOf(value));
            case "isAtTheYard" -> bookingRepository.updateIsAtTheYard(id, Boolean.valueOf(value));
            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        }

        SecurityContextHolder.clearContext();

        List<String> topics = new ArrayList<>(List.of("/topic/unloading-report/" + date, "/topic/forklift-socket"));

        if((
                field.equals("status") && update.valueName().equals(STANDARD_STATUS_NAME))
                ||
                (!field.equals("status") && currentStatus.equals(STANDARD_STATUS_NAME))
        ) {
            topics.add("/topic/yard-man");
        } else if(field.equals("status") && currentStatus.equals(STANDARD_STATUS_NAME)) {
            eventPublisher.publishEvent(new BookingDeleteEvent(id, "RECORD_DELETE", date, List.of("/topic/yard-man")));
        }

        if(field.equals("status") && update.valueName().equals(STANDARD_STATUS_NAME)) {
            eventPublisher.publishEvent(new BookingDeleteEvent(id, "RECORD_DELETE", date, List.of("/topic/forklift-socket")));
        }

        eventPublisher.publishEvent(new BookingCreatedEvent(id, "RECORD_UPDATE", date, topics));
    }

    @Override
    @Transactional
    @AccessControl(
            minWeight = 50
    )
    public void updateControlObjectForkLift(ForkliftControlUpdate update, Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        CustomUserDetails userDetails = securityUtils.getCurrentUser();
        Integer userId = userDetails.id();

        String action = update.action();
        String command = update.type();
        Integer value = update.value();
        String comment = update.comment();
        LocalDate dateBooking = update.dateBooking();
        List<Long> bookingIds = update.bookingIds();

        List<StatusDTO> statuses = statusService.getAllDTO();

        Map<Long, RedisBookingDTO> informationAboutBooking = getBooingFromCache(bookingIds);

        if(command.equals("EDIT_COMMAND") || (command.equals("CONTROL_COMMAND") && !List.of("START_UNLOADING", "RESUME_UNLOADING").contains(action))) {
            if(!bookingIds.stream().allMatch(eachId -> {
                return informationAboutBooking.containsKey(eachId) && informationAboutBooking.get(eachId).isStart;
            })){
                throw new IllegalArgumentException("Unsupported action. All Booking must be start!");
            }
        }

        setCurrentUserId();

        if(command.equals("EDIT_COMMAND")){

            RedisBookingDTO currentBooking = informationAboutBooking.getOrDefault(bookingIds.getFirst(), RedisBookingDTO.builder().build());

            if (!Objects.equals(currentBooking.whoProcessingId, userId)){
                throw new IllegalArgumentException("Unsupported action. Others users");
            }

            switch (action) {
                case "actualEuPal" -> {
                    bookingRepository.updateActualEuPal(bookingIds.getFirst(), value);
                }
                case "actualEuPalDefect" -> {
                    bookingRepository.updateActualEuPalDefect(bookingIds.getFirst(), Integer.valueOf(value.toString()));
                }
                case "actualOnewayPal" -> {
                    bookingRepository.updateActualOnewayPal(bookingIds.getFirst(), Integer.valueOf(value.toString()));
                }
                case "actualColi" -> {
                    bookingRepository.updateActualColi(bookingIds.getFirst(), Integer.valueOf(value.toString()));
                }

                default -> throw new IllegalArgumentException("Unsupported action: " + action);
            }
        }else if(command.equals("CONTROL_COMMAND")){
            switch (action) {
                case "START_UNLOADING", "RESUME_UNLOADING" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("W trakcie rozładunku")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();
                    LocalTime st = action.equals("START_UNLOADING") ? LocalTime.now() : null;
                    int updated = bookingRepository.updateStartOrResume(bookingIds, entityManager.getReference(Status.class, statusId), entityManager.getReference(User.class, userId), st, LocalTime.now());

                    if (updated != bookingIds.size()) {
                        throw new IllegalStateException("Some bookings don't have RAMP assigned");
                    }

                    emitCacheUpdate(bookingIds, userId, "W trakcie rozładunku", true);
                }
                case "PAUSE_UNLOADING" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Pauza")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();
                    bookingRepository.updateStatusesPause(bookingIds, entityManager.getReference(Status.class, statusId));

                    emitCacheUpdate(bookingIds, userId, "Pauza", false);
                }
                case "SET_STATUS_NO_REJECTION" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Rozładowane")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();
                    bookingRepository.updateStatuses(bookingIds, entityManager.getReference(Status.class, statusId));
                }
                case "END_UNLOADING" -> {

                    Optional<Object[]> rowOpt = bookingRepository.endAndUpdateRampsReturnId(bookingIds, LocalTime.now(), "ENABLED");
                    RampDTO ramp = rowOpt
                            .filter(row -> row.length > 0 && row[0] != null)
                            .stream().findFirst()
                            .map(raw -> {
                                Object[] row = (raw.length == 1 && raw[0] instanceof Object[]) ? (Object[]) raw[0] : raw;
                                return rampService.convertObjectRowToRampDTO(row);
                            })
                            .orElseThrow(() -> new IllegalStateException("No ramp updated for given bookings"));

                    rampService.notifyUnloadingReportConfigUpdate(ramp);
                }
                case "SET_STATUS_REJECTION_SN" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Odrzucone")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();
                    setRefectionStatusBooking(value, comment, bookingIds, statusId);
                }
                case "SET_STATUS_REJECTION_QUALITY" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Rozładowane")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();
                    setRefectionStatusBooking(value, comment, bookingIds, statusId);
                }

                default -> throw new IllegalArgumentException("Unsupported action: " + action);
            }
        }

        SecurityContextHolder.clearContext();

        eventPublisher.publishEvent(new BookingCreatedEvent(bookingIds, "RECORD_UPDATE", LocalDate.now(), List.of("/topic/unloading-report/" + dateBooking, "/topic/forklift-socket")));
    }

    private void emitCacheUpdate(List<Long> bookingIds, Integer userId, String statusCode, boolean started) {
        eventPublisher.publishEvent(new BookingCacheUpdateEvent(bookingIds, userId, statusCode, started));
    }

    private void setRefectionStatusBooking(Integer value, String comment, List<Long> bookingIds, Integer statusId) {
        Status statusRef    = entityManager.getReference(Status.class, statusId);
        TypeError typeErrorRef = (value != null) ? entityManager.getReference(TypeError.class, value) : null;
        String commentSet   = (comment != null && !comment.isEmpty()) ? comment : null;

        bookingRepository.updateStatusTypeErrorComment(
                bookingIds.getFirst(),
                statusRef,
                typeErrorRef,
                commentSet
        );
    }

    @Override
    @Transactional
    @AccessControl(
            minWeight = 70
    )
    public void updateControlObjectYardMan(YardManControlUpdate update, Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        setCurrentUserId();

        String type = update.type();
        String action = update.action();
        String value = update.value();
        LocalDate dateBooking = update.dateBooking();
        Long bookingId = update.bookingId();

        List<StatusDTO> statuses = statusService.getAllDTO();

        if(type.equals("UPDATE_FIELD")){
            switch (action) {

                case "isInTheYard" -> {
                    bookingRepository.updateIsInTheYard(bookingId, Boolean.valueOf(value));
                }
                case "comments" -> {
                    bookingRepository.updateComments(bookingId, value);
                }
                case "ramp" -> {
                    Integer statusId = statuses.stream().filter(stat -> stat.getName().equals("Gotowe do rozładunku")).findFirst().orElseThrow(() -> new NotFoundException("Status not found")).getId();

                    String currentStatus = rampService.getStatusNameById(Integer.parseInt(value));

                    if (!currentStatus.equals(KEY_STATUS_RAMP)) {
                        throw new IllegalStateException("Ramp don't free. You can't put this ramp");
                    }

                    bookingRepository.updateRamp(bookingId, entityManager.getReference(Ramp.class, Integer.parseInt(value)), entityManager.getReference(Status.class, statusId), LocalTime.now());
                    RampDTO ramp = rampService.setStatusToBramId(Integer.parseInt(value), StatusBramAndRamp.OCCUPIED);
                    rampService.notifyUnloadingReportConfigUpdate(ramp);
                }

                default -> throw new IllegalArgumentException("Unsupported action: " + action);
            }
        }

        SecurityContextHolder.clearContext();

        eventPublisher.publishEvent(new BookingCreatedEvent(bookingId, "RECORD_UPDATE", LocalDate.now(), List.of("/topic/unloading-report/" + dateBooking, "/topic/forklift-socket", "/topic/yard-man")));

    }

    @Override
    public Map<Long, RedisBookingDTO> getBooingFromCache(List<Long> ids) {

        if (ids == null || ids.isEmpty()) return Map.of();

        List<Long> distinctIds = ids.stream().distinct().toList();

        Map<Long, RedisBookingDTO> result =
                redisCacheService.multiGetBookingsByIds(distinctIds, RedisBookingDTO.class);

        List<Long> missing = distinctIds.stream()
                .filter(id -> !result.containsKey(id))
                .toList();

        if (!missing.isEmpty()) {
            List<BookingDTO> rows = getBookingsByIds(missing);

            for (BookingDTO dto : rows) {
                RedisBookingDTO snap = RedisBookingDTO.builder()
                        .id(dto.getId())
                        .actualStatus(dto.getStatusName())
                        .rampName(dto.getRampName())
                        .isStart(Set.of("W trakcie rozładunku", "Odrzucone", "Problem jakosciowe", "Rozładowane").contains(dto.getStatusName()))
                        .whoProcessingId(dto.getWhoProcessingId())
                        .build();

                result.put(dto.getId(), snap);
                redisCacheService.saveToCacheWithTTL(String.format("booking:%d:idc", dto.getId()), snap, Duration.ofMinutes(15));
            }
        }
        return result;
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
    public void updateRamp(Long id, Integer rampId, Integer statusId, LocalTime time) {
        setCurrentUserId();
        bookingRepository.updateRamp(id, entityManager.getReference(Ramp.class, rampId), entityManager.getReference(Status.class, statusId), time);
    }

    @Override
    @Transactional
    public void updateBram(Long id, Integer bramId) {
        setCurrentUserId();
        bookingRepository.updateBram(id, entityManager.getReference(Bram.class, bramId));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer statusId) {
        setCurrentUserId();
        bookingRepository.updateStatus(id, entityManager.getReference(Status.class, statusId));
    }

    @Override
    @Transactional
    public void updateDeliveryType(Long id, Integer deliveryTypeId) {
        setCurrentUserId();
        bookingRepository.updateDeliveryType(id, entityManager.getReference(DeliveryType.class, deliveryTypeId));
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
        bookingRepository.updateProductType(id, entityManager.getReference(ProductType.class, productTypeId));
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
        bookingRepository.updateProcessType(id, entityManager.getReference(ProcessType.class, updateProcessTypeId));
    }

    @Override
    @Transactional
    public void updateSupplierType(Long id, Integer supplierTypeId) {
        setCurrentUserId();
        bookingRepository.updateSupplierType(id, entityManager.getReference(SupplierType.class, supplierTypeId));
    }

    @Override
    @Transactional
    public void updatePalletExchange(Long id, Integer palletExchange) {
        setCurrentUserId();
        bookingRepository.updatePalletExchange(id, entityManager.getReference(PalletExchange.class, palletExchange));
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
        bookingRepository.updateTypeError(id, entityManager.getReference(TypeError.class, typeErrorId));
    }

    @Override
    @Transactional
    public void updateStartTime(Long id, LocalTime startTime) {
        setCurrentUserId();
        bookingRepository.updateStartTime(List.of(id), startTime);
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
        bookingRepository.updateWhoProcessing(id, entityManager.getReference(User.class, userId));
    }

    private void setCurrentUserId() {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userDetails.id()))
                .getSingleResult();
    }
}

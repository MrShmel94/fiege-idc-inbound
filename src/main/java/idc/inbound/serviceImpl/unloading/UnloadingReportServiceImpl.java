package idc.inbound.serviceImpl.unloading;

import idc.inbound.configuration.BookingCreatedEvent;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.*;
import idc.inbound.entity.unloading.*;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.request.BookingAddRequest;
import idc.inbound.response.UnloadingReportResponse;
import idc.inbound.response.UserConfigResponse;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.unloading.*;
import idc.inbound.service.vision.RoleService;
import idc.inbound.serviceImpl.vision.DepartmentServiceImpl;
import idc.inbound.serviceImpl.vision.PositionServiceImpl;
import idc.inbound.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnloadingReportServiceImpl implements UnloadingReportService {

    private final SecurityUtils securityUtils;
    private final Utils utils;
    private final BookingService bookingService;
    private final BramService bramService;
    private final DeliveryTypeServiceImpl deliveryTypeService;
    private final PalletExchangeServiceImpl palletExchangeService;
    private final ProcessTypeServiceImpl processTypeService;
    private final ProductTypeServiceImpl productTypeService;
    private final RampService rampService;
    private final StatusServiceImpl statusService;
    private final SupplierTypeServiceImpl supplierTypeService;
    private final TypeErrorServiceImpl typeErrorService;

    private final DepartmentServiceImpl departmentService;
    private final PositionServiceImpl positionService;
    private final RoleService roleService;

    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserConfigResponse getConfigByUser(){
        return UserConfigResponse.builder()
                .roles(roleService.getAllRoles())
                .departments(departmentService.getAllDTO())
                .positions(positionService.getAllDTO())
                .build();
    }

    @Override
    public UnloadingReportResponse getConfig() {
        return UnloadingReportResponse.builder()
                .bram(bramService.getAllDTO())
                .deliveryType(deliveryTypeService.getAllDTO())
                .palletExchange(palletExchangeService.getAllDTO())
                .processType(processTypeService.getAllDTO())
                .productType(productTypeService.getAllDTO())
                .typeError(typeErrorService.getAllDTO())
                .ramp(rampService.getAllDTO())
                .status(statusService.getAllDTO())
                .supplierType(supplierTypeService.getAllDTO())
                .build();
    }

    @Override
    @Transactional
    public List<String> uploadFileReport(MultipartFile file, LocalDate date) {
        utils.validateHeaders(file);
        List<BookingCSV> csvDTO = utils.csvUnloadingReport(file);
        List<Booking> entityList = new ArrayList<>();

        Map<String, Integer> deliveryTypeMap = deliveryTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(DeliveryTypeDTO::getName, DeliveryTypeDTO::getId));

        Map<String, Integer> productTypeMap = productTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(ProductTypeDTO::getName, ProductTypeDTO::getId));

        Map<String, Integer> processTypeMap = processTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(ProcessTypeDTO::getName, ProcessTypeDTO::getId));

        Map<String, Integer> supplierTypeMap = supplierTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(SupplierTypeDTO::getName, SupplierTypeDTO::getId));

        Map<String, Integer> palletExchangeMap = palletExchangeService.getAllDTO()
                .stream().collect(Collectors.toMap(PalletExchangeDTO::getName, PalletExchangeDTO::getId));

        List<String> errors = new ArrayList<>();

        for (int i = 0; i < csvDTO.size(); i++) {
            BookingCSV bookingCSV = csvDTO.get(i);

            Booking entity = new Booking();
            entity.setDate(date);

            Integer idDeliveryType = deliveryTypeMap.get(bookingCSV.getDeliveryType());

            if (idDeliveryType == null) {
                errors.add(String.format("Incorrect Delivery Type type %s in row %d", bookingCSV.getDeliveryType(), i + 2));
                continue;
            }

            entity.setDeliveryType(entityManager.getReference(DeliveryType.class, idDeliveryType));
            entity.setQtyPal(checkMoreZero(bookingCSV.getQtyPalTotal()));
            entity.setQtyBoxes(checkMoreZero(bookingCSV.getQtyBoxTotal()));
            entity.setQtyItems(checkMoreZero(bookingCSV.getQtyItemTotal()));

            if (bookingCSV.getEstimatedArrivalTime().isEmpty()) {
                errors.add(String.format("Estimated Arrival Time is empty in row %d", i + 2));
                continue;
            }

            entity.setEstimatedArrivalTime(parseTimeFlexible(bookingCSV.getEstimatedArrivalTime()));

            if (bookingCSV.getNotificationNumber().isEmpty()) {
                errors.add(String.format("Notification Number is empty in row %d", i + 2));
                continue;
            }

            entity.setNotificationNumber(bookingCSV.getNotificationNumber());

            if (bookingCSV.getBooking().isEmpty()) {
                errors.add(String.format("Booking is empty in row %d", i + 2));
                continue;
            }

            entity.setBookingId(bookingCSV.getBooking());

            Integer idProductType = productTypeMap.get(bookingCSV.getProductType());

            if (idProductType == null) {
                errors.add(String.format("Incorrect Product Type type %s in row %d", bookingCSV.getProductType(), i + 2));
                continue;
            }

            entity.setProductType(entityManager.getReference(ProductType.class, idProductType));

            if (!bookingCSV.getProcessType().isEmpty()) {
                Integer idProcessType = processTypeMap.get(bookingCSV.getProcessType());

                if (idProcessType == null) {
                    errors.add(String.format("Incorrect Process Type type %s in row %d", bookingCSV.getProcessType(), i + 2));
                    continue;
                }

                entity.setProcessType(entityManager.getReference(ProcessType.class, idProcessType));
            }

            if (!bookingCSV.getSupplierType().isEmpty()) {
                Integer idSupplierType = supplierTypeMap.get(bookingCSV.getSupplierType());

                if (idSupplierType == null) {
                    errors.add(String.format("Incorrect Supplier Type type %s in row %d", bookingCSV.getSupplierType(), i + 2));
                    continue;
                }

                entity.setSupplierType(entityManager.getReference(SupplierType.class, idSupplierType));
            }

            Integer idPalletExchangeType = palletExchangeMap.get(bookingCSV.getPalletExchange());

            if (idPalletExchangeType == null) {
                errors.add(String.format("Incorrect Pallet Exchange Type type %s in row %d", bookingCSV.getPalletExchange(), i + 2));
                continue;
            }

            entity.setPalletExchange(entityManager.getReference(PalletExchange.class, idPalletExchangeType));

            entityList.add(entity);
        }

        if (!entityList.isEmpty()) {
            setCurrentUserId();

            List<Booking> savedEntities = bookingService.saveAllBookings(entityList);
            List<Long> ids = savedEntities.stream().map(Booking::getId).toList();

            eventPublisher.publishEvent(new BookingCreatedEvent(ids, "RECORD_ADD", date));
        }
        return errors;
    }

    @Override
    @Transactional
    public void uploadBooking(LocalDate date, BookingAddRequest request) {

        Map<String, Integer> deliveryTypeMap = deliveryTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(DeliveryTypeDTO::getName, DeliveryTypeDTO::getId));

        Map<String, Integer> productTypeMap = productTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(ProductTypeDTO::getName, ProductTypeDTO::getId));

        Map<String, Integer> processTypeMap = processTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(ProcessTypeDTO::getName, ProcessTypeDTO::getId));

        Map<String, Integer> supplierTypeMap = supplierTypeService.getAllDTO()
                .stream().collect(Collectors.toMap(SupplierTypeDTO::getName, SupplierTypeDTO::getId));

        Map<String, Integer> palletExchangeMap = palletExchangeService.getAllDTO()
                .stream().collect(Collectors.toMap(PalletExchangeDTO::getName, PalletExchangeDTO::getId));

        Booking entity = new Booking();
        entity.setDate(date);

        Integer idDeliveryType = deliveryTypeMap.get(request.deliveryType());

        if (idDeliveryType == null) {
            throw new NotFoundException("Delivery Type not found " + request.deliveryType());
        }

        entity.setDeliveryType(entityManager.getReference(DeliveryType.class, idDeliveryType));

        entity.setQtyPal(checkMoreZero(request.qtyPalTotal()));
        entity.setQtyBoxes(checkMoreZero(request.qtyBoxTotal()));
        entity.setQtyItems(checkMoreZero(request.qtyItemTotal()));

        entity.setEstimatedArrivalTime(parseTimeFlexible(request.estimatedArrivalTime()));
        entity.setNotificationNumber(request.notificationNumber());
        entity.setBookingId(request.booking());

        Integer idProductType = productTypeMap.get(request.productType());

        if (idProductType == null) {
            throw new NotFoundException("Product Type not found " + request.productType());
        }
        entity.setProductType(entityManager.getReference(ProductType.class, idProductType));

        if (!request.processType().isEmpty()) {
            Integer idProcessType = processTypeMap.get(request.processType());

            if (idProcessType == null) {
                throw new NotFoundException("Process type not found " + request.processType());
            }

            entity.setProcessType(entityManager.getReference(ProcessType.class, idProcessType));
        }

        if (!request.supplierType().isEmpty()) {
            Integer idSupplierType = supplierTypeMap.get(request.supplierType());

            if (idSupplierType == null) {
                throw new NotFoundException("Supplier Type not found " + request.supplierType());
            }

            entity.setSupplierType(entityManager.getReference(SupplierType.class, idSupplierType));
        }

        Integer idPalletExchangeType = palletExchangeMap.get(request.palletExchange());

        if (idPalletExchangeType == null) {
            throw new NotFoundException("Pallet Exchange Type not found " + request.palletExchange());
        }

        entity.setPalletExchange(entityManager.getReference(PalletExchange.class, idPalletExchangeType));

        setCurrentUserId();
        bookingService.save(entity);

        eventPublisher.publishEvent(new BookingCreatedEvent(entity.getId(), "RECORD_ADD", entity.getDate()));
    }

    private int checkMoreZero(int number) {
        return Math.max(number, 0);
    }

    private LocalTime parseTimeFlexible(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        return LocalTime.parse(timeString.trim(), formatter);
    }

    @Override
    public List<BookingDTO> getReportByDate(LocalDate date) {
        return bookingService.getAllBookingsChoiceDate(date);
    }

    private void setCurrentUserId() {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userDetails.id()))
                .getSingleResult();
    }
}

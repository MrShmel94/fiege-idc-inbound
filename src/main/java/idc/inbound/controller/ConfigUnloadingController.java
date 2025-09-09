package idc.inbound.controller;


import idc.inbound.dto.unloading.*;
import idc.inbound.dto.vision.DepartmentDTO;
import idc.inbound.dto.vision.PositionDTO;
import idc.inbound.request.UnloadingChangeBramRampRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.request.UnloadingSaveBramRampRequest;
import idc.inbound.response.UserConfigResponse;
import idc.inbound.service.unloading.*;
import idc.inbound.serviceImpl.unloading.*;
import idc.inbound.serviceImpl.vision.DepartmentServiceImpl;
import idc.inbound.serviceImpl.vision.PositionServiceImpl;
import idc.inbound.serviceImpl.vision.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/config_app")
@RequiredArgsConstructor
@Slf4j
public class ConfigUnloadingController {

    private final BramService bramService;
    private final RampService rampService;

    private final PalletExchangeServiceImpl palletExchangeService;
    private final DeliveryTypeServiceImpl deliveryTypeService;
    private final ProcessTypeServiceImpl processTypeService;
    private final ProductTypeServiceImpl productTypeService;
    private final TypeErrorServiceImpl typeErrorService;
    private final SupplierTypeServiceImpl supplierTypeService;
    private final PositionServiceImpl positionService;
    private final DepartmentServiceImpl departmentService;
    private final UnloadingReportService unloadingReportService;

    @GetMapping("/getConfigForUser")
    public ResponseEntity<UserConfigResponse> getConfigForUser() {
        return ResponseEntity.ok(unloadingReportService.getConfigByUser());
    }

    @GetMapping("/getAllSuppliersType")
    public ResponseEntity<List<SupplierTypeDTO>> getAllSupplierTypes() {
        return ResponseEntity.ok(supplierTypeService.getAllDTO());
    }

    @PostMapping("/getAllSuppliersType/save")
    public ResponseEntity<?> saveSupplierTypes(@RequestBody UnloadingRequestModal request) {
        supplierTypeService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllSuppliersType/update")
    public ResponseEntity<?> updateSupplierTypes(@RequestBody UnloadingChangeRequestModal request) {
        supplierTypeService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllProductType")
    public ResponseEntity<List<ProductTypeDTO>> getAllProductType() {
        return ResponseEntity.ok(productTypeService.getAllDTO());
    }

    @PostMapping("/getAllProductType/save")
    public ResponseEntity<?> saveProductType(@RequestBody UnloadingRequestModal request) {
        productTypeService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllProductType/update")
    public ResponseEntity<?> updateProductType(@RequestBody UnloadingChangeRequestModal request) {
        productTypeService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllProcessType")
    public ResponseEntity<List<ProcessTypeDTO>> getAllProcessType() {
        return ResponseEntity.ok(processTypeService.getAllDTO());
    }

    @PostMapping("/getAllProcessType/save")
    public ResponseEntity<?> saveProcessType(@RequestBody UnloadingRequestModal request) {
        processTypeService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllProcessType/update")
    public ResponseEntity<?> updateProcessType(@RequestBody UnloadingChangeRequestModal request) {
        processTypeService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllPalletExchangeType")
    public ResponseEntity<List<PalletExchangeDTO>> getAllPalletExchange() {
        return ResponseEntity.ok(palletExchangeService.getAllDTO());
    }

    @PostMapping("/getAllPalletExchangeType/save")
    public ResponseEntity<?> savePalletExchange(@RequestBody UnloadingRequestModal request) {
        palletExchangeService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllPalletExchangeType/update")
    public ResponseEntity<?> updatePalletExchange(@RequestBody UnloadingChangeRequestModal request) {
        palletExchangeService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllBram")
    public ResponseEntity<List<BramDTO>> getAllBram() {
        return ResponseEntity.ok(bramService.getAllDTO());
    }

    @PostMapping("/getAllBram/save")
    public ResponseEntity<?> saveBram(@RequestBody UnloadingSaveBramRampRequest request) {
        bramService.saveNewEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllBram/update")
    public ResponseEntity<?> updateBram(@RequestBody UnloadingChangeBramRampRequest request) {
        bramService.changeEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllRamp")
    public ResponseEntity<List<RampDTO>> getAllRamp() {
        return ResponseEntity.ok(rampService.getAllDTO());
    }

    @PostMapping("/getAllRamp/save")
    public ResponseEntity<?> saveRamp(@RequestBody UnloadingSaveBramRampRequest request) {
        rampService.saveNewEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllRamp/update")
    public ResponseEntity<?> updateRamp(@RequestBody UnloadingChangeBramRampRequest request) {
        rampService.changeEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllDeliveryType")
    public ResponseEntity<List<DeliveryTypeDTO>> getAllDeliveryType() {
        return ResponseEntity.ok(deliveryTypeService.getAllDTO());
    }

    @PostMapping("/getAllDeliveryType/save")
    public ResponseEntity<?> saveDeliveryType(@RequestBody UnloadingRequestModal request) {
        deliveryTypeService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllDeliveryType/update")
    public ResponseEntity<?> updateDeliveryType(@RequestBody UnloadingChangeRequestModal request) {
        deliveryTypeService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllDepartment")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartment() {
        return ResponseEntity.ok(departmentService.getAllDTO());
    }

    @PostMapping("/getAllDepartment/save")
    public ResponseEntity<?> saveDepartment(@RequestBody UnloadingRequestModal request) {
        departmentService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllDepartment/update")
    public ResponseEntity<?> updateDepartment(@RequestBody UnloadingChangeRequestModal request) {
        departmentService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllPosition")
    public ResponseEntity<List<PositionDTO>> getAllPosition() {
        return ResponseEntity.ok(positionService.getAllDTO());
    }

    @PostMapping("/getAllPosition/save")
    public ResponseEntity<?> savePosition(@RequestBody UnloadingRequestModal request) {
        positionService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllPosition/update")
    public ResponseEntity<?> updatePosition(@RequestBody UnloadingChangeRequestModal request) {
        positionService.updateEntity(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllTypeError")
    public ResponseEntity<List<TypeErrorDTO>> getAllTypeError() {
        return ResponseEntity.ok(typeErrorService.getAllDTO());
    }

    @PostMapping("/getAllTypeError/save")
    public ResponseEntity<?> saveTypeError(@RequestBody UnloadingRequestModal request) {
        typeErrorService.saveEntity(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getAllTypeError/update")
    public ResponseEntity<?> updateTypeError(@RequestBody UnloadingChangeRequestModal request) {
        typeErrorService.updateEntity(request);
        return ResponseEntity.ok().build();
    }
}

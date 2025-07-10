package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.StatusDTO;
import idc.inbound.dto.unloading.SupplierTypeDTO;
import idc.inbound.entity.unloading.SupplierType;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.SupplierTypeRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.SupplierTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SupplierTypeServiceImpl extends AbstractCrudServiceUnloading<
        SupplierType,
        SupplierTypeDTO,
        SupplierTypeRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements SupplierTypeService {

    public SupplierTypeServiceImpl(
            SupplierTypeRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "supplier_type_idc";
    }

    @Override
    protected List<SupplierTypeDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected SupplierType createNewEntity() {
        return new SupplierType();
    }

    @Override
    protected void updateEntityFromRequest(SupplierType entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(SupplierType entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<SupplierTypeDTO>> getTypeReference() {
        return new TypeReference<List<SupplierTypeDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "supplierType";
    }

    @Override
    protected SupplierTypeDTO mapToDto(SupplierType supplierType) {
        return SupplierTypeDTO.builder()
                .id(supplierType.getId())
                .name(supplierType.getName())
                .build();
    }
}

package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.DeliveryTypeDTO;
import idc.inbound.dto.unloading.PalletExchangeDTO;
import idc.inbound.entity.unloading.DeliveryType;
import idc.inbound.entity.unloading.PalletExchange;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.DeliveryTypeRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.DeliveryTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeliveryTypeServiceImpl extends AbstractCrudServiceUnloading<
        DeliveryType,
        DeliveryTypeDTO,
        DeliveryTypeRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements DeliveryTypeService {

    public DeliveryTypeServiceImpl(
            DeliveryTypeRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "delivery_type_idc";
    }

    @Override
    protected List<DeliveryTypeDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected DeliveryType createNewEntity() {
        return new DeliveryType();
    }

    @Override
    protected void updateEntityFromRequest(DeliveryType entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(DeliveryType entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<DeliveryTypeDTO>> getTypeReference() {
        return new TypeReference<List<DeliveryTypeDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "deliveryType";
    }

    @Override
    protected DeliveryTypeDTO mapToDto(DeliveryType deliveryType) {
        return DeliveryTypeDTO.builder()
                .id(deliveryType.getId())
                .name(deliveryType.getName())
                .build();
    }
}

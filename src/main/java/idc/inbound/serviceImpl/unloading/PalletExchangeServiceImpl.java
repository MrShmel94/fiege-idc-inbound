package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.PalletExchangeDTO;
import idc.inbound.dto.unloading.ProcessTypeDTO;
import idc.inbound.entity.unloading.PalletExchange;
import idc.inbound.entity.unloading.ProcessType;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.PalletExchangeRepository;
import idc.inbound.repository.unloading.ProcessTypeRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.PalletExchangeService;
import idc.inbound.service.unloading.ProcessTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PalletExchangeServiceImpl extends AbstractCrudServiceUnloading<
        PalletExchange,
        PalletExchangeDTO,
        PalletExchangeRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements PalletExchangeService {

    public PalletExchangeServiceImpl(
            PalletExchangeRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "pallet_exchange_type_idc";
    }

    @Override
    protected List<PalletExchangeDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected PalletExchange createNewEntity() {
        return new PalletExchange();
    }

    @Override
    protected void updateEntityFromRequest(PalletExchange entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(PalletExchange entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<PalletExchangeDTO>> getTypeReference() {
        return new TypeReference<List<PalletExchangeDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "palletExchange";
    }

    @Override
    protected PalletExchangeDTO mapToDto(PalletExchange palletExchange) {
        return PalletExchangeDTO.builder()
                .id(palletExchange.getId())
                .name(palletExchange.getName())
                .build();
    }
}

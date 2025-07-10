package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.PalletExchangeDTO;
import idc.inbound.dto.unloading.ProcessTypeDTO;
import idc.inbound.dto.unloading.ProductTypeDTO;
import idc.inbound.entity.unloading.ProcessType;
import idc.inbound.entity.unloading.ProductType;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.ProcessTypeRepository;
import idc.inbound.repository.unloading.ProductTypeRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.ProcessTypeService;
import idc.inbound.service.unloading.ProductTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProcessTypeServiceImpl extends AbstractCrudServiceUnloading<
        ProcessType,
        ProcessTypeDTO,
        ProcessTypeRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements ProcessTypeService {

    public ProcessTypeServiceImpl(
            ProcessTypeRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "process_type_idc";
    }

    @Override
    protected List<ProcessTypeDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected ProcessType createNewEntity() {
        return new ProcessType();
    }

    @Override
    protected void updateEntityFromRequest(ProcessType entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(ProcessType entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<ProcessTypeDTO>> getTypeReference() {
        return new TypeReference<List<ProcessTypeDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "processType";
    }

    @Override
    protected ProcessTypeDTO mapToDto(ProcessType processType) {
        return ProcessTypeDTO.builder()
                .id(processType.getId())
                .name(processType.getName())
                .build();
    }
}

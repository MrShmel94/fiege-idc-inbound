package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.ProductTypeDTO;
import idc.inbound.dto.unloading.StatusDTO;
import idc.inbound.entity.unloading.Status;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.StatusRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StatusServiceImpl extends AbstractCrudServiceUnloading<
        Status,
        StatusDTO,
        StatusRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements StatusService {

    public StatusServiceImpl(
            StatusRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "status_type_idc";
    }

    @Override
    protected List<StatusDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected Status createNewEntity() {
        return new Status();
    }

    @Override
    protected void updateEntityFromRequest(Status entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(Status entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<StatusDTO>> getTypeReference() {
        return new TypeReference<List<StatusDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "status";
    }

    @Override
    protected StatusDTO mapToDto(Status status) {
        return StatusDTO.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }
}

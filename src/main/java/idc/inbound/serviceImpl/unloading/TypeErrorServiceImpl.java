package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.TypeErrorDTO;
import idc.inbound.entity.unloading.TypeError;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.TypeErrorRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.TypeErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TypeErrorServiceImpl extends AbstractCrudServiceUnloading<
        TypeError,
        TypeErrorDTO,
        TypeErrorRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements TypeErrorService {

    public TypeErrorServiceImpl(
            TypeErrorRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "error_type_idc";
    }

    @Override
    protected List<TypeErrorDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected TypeError createNewEntity() {
        return new TypeError();
    }

    @Override
    protected void updateEntityFromRequest(TypeError entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(TypeError entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<TypeErrorDTO>> getTypeReference() {
        return new TypeReference<List<TypeErrorDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "typeError";
    }

    @Override
    protected TypeErrorDTO mapToDto(TypeError entity) {
        return TypeErrorDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}

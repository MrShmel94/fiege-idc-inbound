package idc.inbound.serviceImpl.vision;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.CacheUpdateException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.vision.DepartmentDTO;
import idc.inbound.dto.vision.PositionDTO;
import idc.inbound.entity.vision.Department;
import idc.inbound.entity.vision.Position;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.vision.DepartmentRepository;
import idc.inbound.repository.vision.PositionRepository;
import idc.inbound.request.DepartmentOrPositionRequest;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.vision.DepartmentService;
import idc.inbound.service.vision.PositionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PositionServiceImpl extends AbstractCrudServiceUnloading<
        Position,
        PositionDTO,
        PositionRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements PositionService {

    public PositionServiceImpl(
            PositionRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "position_idc";
    }

    @Override
    protected List<PositionDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected Position createNewEntity() {
        return new Position();
    }

    @Override
    protected void updateEntityFromRequest(Position entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(Position entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<PositionDTO>> getTypeReference() {
        return new TypeReference<List<PositionDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "position";
    }

    @Override
    protected PositionDTO mapToDto(Position entity) {
        return PositionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
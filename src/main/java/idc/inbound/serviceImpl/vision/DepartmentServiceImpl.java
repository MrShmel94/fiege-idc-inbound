package idc.inbound.serviceImpl.vision;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.CacheUpdateException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.PalletExchangeDTO;
import idc.inbound.dto.vision.DepartmentDTO;
import idc.inbound.entity.unloading.PalletExchange;
import idc.inbound.entity.vision.Department;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.PalletExchangeRepository;
import idc.inbound.repository.vision.DepartmentRepository;
import idc.inbound.request.DepartmentOrPositionRequest;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.PalletExchangeService;
import idc.inbound.service.vision.DepartmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DepartmentServiceImpl extends AbstractCrudServiceUnloading<
        Department,
        DepartmentDTO,
        DepartmentRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements DepartmentService {

    public DepartmentServiceImpl(
            DepartmentRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "department_idc";
    }

    @Override
    protected List<DepartmentDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected Department createNewEntity() {
        return new Department();
    }

    @Override
    protected void updateEntityFromRequest(Department entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(Department entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<DepartmentDTO>> getTypeReference() {
        return new TypeReference<List<DepartmentDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "department";
    }

    @Override
    protected DepartmentDTO mapToDto(Department entity) {
        return DepartmentDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}

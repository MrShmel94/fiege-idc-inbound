package idc.inbound.service;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.CacheUpdateException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.NameRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCrudServiceUnloading <
        ENTITY,
        DTO,
        REPO extends NameRepository<ENTITY, Integer>,
        SaveRequest extends NameChangeRequest,
        UpdateRequest extends NameChangeRequest
        > {

    protected final REPO repository;
    protected final RedisCacheService redisCacheService;
    protected final SecurityUtils securityUtils;
    protected final SimpMessagingTemplate simpMessagingTemplate;

    public AbstractCrudServiceUnloading(REPO repository,
                               RedisCacheService redisCacheService,
                               SecurityUtils securityUtils, SimpMessagingTemplate simpMessagingTemplate) {
        this.repository = repository;
        this.redisCacheService = redisCacheService;
        this.securityUtils = securityUtils;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    protected abstract String getRedisKey();
    protected abstract List<DTO> getAllDTOImpl();
    protected abstract ENTITY createNewEntity();
    protected abstract void updateEntityFromRequest(ENTITY entity, NameChangeRequest request);

    public List<DTO> getAllDTO() {
        return redisCacheService.getFromCache(getRedisKey(), getTypeReference()).orElseGet(() -> {
            List<DTO> allDto = getAllDTOImpl();
            redisCacheService.saveToCache(getRedisKey(), allDto);
            return allDto;
        });
    }

    @Transactional
    public void saveEntity(SaveRequest requestModal) {
        Optional<ENTITY> lookFor = repository.findByName(requestModal.getNewName());
        if(lookFor.isPresent()) {
            throw new AlreadyExistsException("Entity already exists");
        }
        saveToDataBase(requestModal, createNewEntity());
    }

    @Transactional
    public void updateEntity(UpdateRequest requestModal) {
        Optional<ENTITY> lookFor = repository.findById(requestModal.getId());
        if(lookFor.isEmpty()) {
            throw new NotFoundException("Entity not exists");
        }
        ENTITY entity = lookFor.get();
        saveToDataBase(requestModal, entity);
    }

    protected <T extends NameChangeRequest> void saveToDataBase(T requestModal, ENTITY entity) {
        CustomUserDetails user = securityUtils.getCurrentUser();
        updateEntityFromRequest(entity, requestModal);
        setUserId(entity, user.getId());
        repository.save(entity);

        try {
            List<DTO> allDto = getAllDTOImpl();
            redisCacheService.saveToCache(getRedisKey(), allDto);

            DTO updatedDto = mapToDto(entity);
            notifyUnloadingReportConfigUpdate(updatedDto);
        } catch (Exception e) {
            redisCacheService.removeFromCache(getRedisKey());
            throw new CacheUpdateException("Failed to update Redis cache");
        }
    }

    protected void notifyUnloadingReportConfigUpdate(DTO dto) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", getConfigType());
        message.put("data", dto);
        simpMessagingTemplate.convertAndSend("/topic/unloading-report/configUpdate", message);
    }

    protected abstract void setUserId(ENTITY entity, Integer userId);
    protected abstract TypeReference<List<DTO>> getTypeReference();
    protected abstract String getConfigType();
    protected abstract DTO mapToDto(ENTITY entity);
}

package idc.inbound.serviceImpl.unloading;

import idc.inbound.configuration.WebSocketSubscriptionRegistry;
import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import idc.inbound.mapper.BramRampMapping;
import idc.inbound.repository.unloading.BramRepository;
import idc.inbound.request.UnloadingChangeBramRampRequest;
import idc.inbound.request.UnloadingSaveBramRampRequest;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.unloading.BramService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BramServiceImpl implements BramService {

    private final BramRepository bramRepository;
    private final SecurityUtils securityUtils;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public BramDTO changeEntity(UnloadingChangeBramRampRequest request) {
        Optional<Bram> entityRaw = findById(request.id());

        if(entityRaw.isEmpty()) {
            throw new NotFoundException("Bram this id not found");
        }

        Bram entity = entityRaw.get();

        if(!entity.getName().equals(request.oldName())){
            throw new NotFoundException("Bram this name not match");
        }

        entity.setName(request.newName());
        entity.setActualBuffer(request.currentBuffer());
        entity.setMaxBuffer(request.maxBuffer());
        entity.setStatus(StatusBramAndRamp.fromStringOrThrow(request.status()));

        bramRepository.save(entity);

        BramDTO dto = BramRampMapping.INSTANCE.toBramDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    @Transactional
    public BramDTO saveNewEntity(UnloadingSaveBramRampRequest request) {
        Optional<Bram> entityBase = bramRepository.findByName(request.name());

        if (entityBase.isPresent()) {
            throw new AlreadyExistsException("Bram with name " + request.name() + " already exists");
        }

        Bram entity = new Bram();
        entity.setName(request.name());
        entity.setStatus(StatusBramAndRamp.ENABLED);
        entity.setActualBuffer(0);
        entity.setMaxBuffer(request.maxBuffer());

        bramRepository.save(entity);

        BramDTO dto = BramRampMapping.INSTANCE.toBramDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    @Transactional
    public BramDTO changeStatusById(Integer id, StatusBramAndRamp status) {

        if(id == null){
            throw new NotFoundException("id is null");
        }

        Bram entity = findById(id).orElseThrow(() -> new NotFoundException("Bram not found"));

        setCurrentUserId();

        entity.setStatus(status);
        bramRepository.save(entity);

        BramDTO dto = BramRampMapping.INSTANCE.toBramDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    public List<BramDTO> getAllDTO() {
        return bramRepository.findAllDTO();
    }

    @Override
    public List<BramDTO> getAllAvailableDTO() {
        return bramRepository.findAllSelectStatusDTO(StatusBramAndRamp.ENABLED);
    }

    @Override
    public List<BramDTO> getAllDisableDTO() {
        return bramRepository.findAllSelectStatusDTO(StatusBramAndRamp.DISABLED);
    }

    @Override
    public List<BramDTO> getAllOccupiedDTO() {
        return bramRepository.findAllSelectStatusDTO(StatusBramAndRamp.OCCUPIED);
    }

    @Override
    public BramDTO getDTOById(Integer id) {
        return bramRepository.findByIdDTO(id).orElseThrow(() -> new NotFoundException("Bram by id not found"));
    }

    @Override
    public BramDTO getDTOByName(String name) {
        return bramRepository.findByNameDTO(name).orElseThrow(() -> new NotFoundException("Bram by name not found"));
    }

    @Override
    public Optional<Bram> findById(Integer id) {
        return bramRepository.findById(id);
    }

    private void setCurrentUserId() {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userDetails.id()))
                .getSingleResult();
    }

    private void notifyUnloadingReportConfigUpdate(Object configDto) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "bram");
        message.put("data", configDto);

        simpMessagingTemplate.convertAndSend("/topic/unloading-report/configUpdate", message);
    }
}

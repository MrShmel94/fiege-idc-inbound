package idc.inbound.serviceImpl.unloading;

import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.dto.unloading.RampDTO;
import idc.inbound.entity.unloading.Ramp;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import idc.inbound.mapper.BramRampMapping;
import idc.inbound.repository.unloading.RampRepository;
import idc.inbound.request.UnloadingChangeBramRampRequest;
import idc.inbound.request.UnloadingSaveBramRampRequest;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.unloading.RampService;
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
public class RampServiceImpl implements RampService {

    private final RampRepository rampRepository;
    private final SecurityUtils securityUtils;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public void notifyUnloadingReportConfigUpdate(Object configDto) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ramp");
        message.put("data", configDto);

        simpMessagingTemplate.convertAndSend("/topic/unloading-report/configUpdate", message);
    }

    @Override
    @Transactional
    public RampDTO changeEntity(UnloadingChangeBramRampRequest request) {
        Optional<Ramp> entityRaw = findById(request.id());

        if(entityRaw.isEmpty()) {
            throw new NotFoundException("Ramp this id not found");
        }

        Ramp entity = entityRaw.get();

        if(!entity.getName().equals(request.oldName())){
            throw new NotFoundException("Ramp this name not match");
        }

        entity.setName(request.newName());
        entity.setActualBuffer(request.currentBuffer());
        entity.setMaxBuffer(request.maxBuffer());
        entity.setStatus(StatusBramAndRamp.fromStringOrThrow(request.status()));

        rampRepository.save(entity);

        RampDTO dto = BramRampMapping.INSTANCE.toRampDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    @Transactional
    public RampDTO saveNewEntity(UnloadingSaveBramRampRequest request) {
        Optional<Ramp> entityBase = rampRepository.findByName(request.name());

        if (entityBase.isPresent()) {
            throw new AlreadyExistsException("Ramp with name " + request.name() + " already exists");
        }

        Ramp entity = new Ramp();
        entity.setName(request.name());
        entity.setStatus(StatusBramAndRamp.ENABLED);
        entity.setActualBuffer(0);
        entity.setMaxBuffer(request.maxBuffer());

        rampRepository.save(entity);

        RampDTO dto = BramRampMapping.INSTANCE.toRampDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    @Transactional
    public RampDTO changeStatusById(Integer id, StatusBramAndRamp status) {

        if(id == null){
            throw new NotFoundException("id is null");
        }

        Ramp entity = findById(id).orElseThrow(() -> new NotFoundException("Ramp not found"));

        setCurrentUserId();

        entity.setStatus(status);
        rampRepository.save(entity);

        RampDTO dto = BramRampMapping.INSTANCE.toRampDTO(entity);

        notifyUnloadingReportConfigUpdate(dto);

        return dto;
    }

    @Override
    public List<RampDTO> getAllDTO() {
        return rampRepository.findAllDTO();
    }

    @Override
    public List<RampDTO> getAllAvailableDTO() {
        return rampRepository.findAllSelectStatusDTO(StatusBramAndRamp.ENABLED);
    }

    @Override
    public List<RampDTO> getAllDisableDTO() {
        return rampRepository.findAllSelectStatusDTO(StatusBramAndRamp.DISABLED);
    }

    @Override
    public List<RampDTO> getAllOccupiedDTO() {
        return rampRepository.findAllSelectStatusDTO(StatusBramAndRamp.OCCUPIED);
    }

    @Override
    public RampDTO getDTOById(Integer id) {
        return rampRepository.findByIdDTO(id).orElseThrow(() -> new NotFoundException("Ramp by id not found"));
    }

    @Override
    public RampDTO getDTOByName(String name) {
        return rampRepository.findByNameDTO(name).orElseThrow(() -> new NotFoundException("Ramp by name not found"));
    }

    @Override
    public Optional<Ramp> findById(Integer id) {
        return rampRepository.findById(id);
    }

    private void setCurrentUserId() {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userDetails.id()))
                .getSingleResult();
    }
}

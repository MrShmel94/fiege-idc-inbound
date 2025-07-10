package idc.inbound.service.unloading;

import idc.inbound.dto.unloading.RampDTO;
import idc.inbound.entity.unloading.Ramp;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import idc.inbound.request.UnloadingChangeBramRampRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingSaveBramRampRequest;

import java.util.List;
import java.util.Optional;

public interface RampService {

    RampDTO changeEntity(UnloadingChangeBramRampRequest request);
    RampDTO saveNewEntity(UnloadingSaveBramRampRequest request);
    RampDTO changeStatusById(Integer id, StatusBramAndRamp status);

    List<RampDTO> getAllDTO();
    List<RampDTO> getAllAvailableDTO();
    List<RampDTO> getAllDisableDTO();
    List<RampDTO> getAllOccupiedDTO();

    RampDTO getDTOById(Integer id);
    RampDTO getDTOByName(String name);

    Optional<Ramp> findById(Integer id);
}

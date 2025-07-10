package idc.inbound.service.unloading;

import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import idc.inbound.request.UnloadingChangeBramRampRequest;
import idc.inbound.request.UnloadingSaveBramRampRequest;

import java.util.List;
import java.util.Optional;

public interface BramService {

    BramDTO changeEntity(UnloadingChangeBramRampRequest request);
    BramDTO saveNewEntity(UnloadingSaveBramRampRequest request);
    BramDTO changeStatusById(Integer id, StatusBramAndRamp status);

    List<BramDTO> getAllDTO();
    List<BramDTO> getAllAvailableDTO();
    List<BramDTO> getAllDisableDTO();
    List<BramDTO> getAllOccupiedDTO();

    BramDTO getDTOById(Integer id);
    BramDTO getDTOByName(String name);

    Optional<Bram> findById(Integer id);

}

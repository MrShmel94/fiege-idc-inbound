package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.ProcessTypeDTO;
import idc.inbound.entity.unloading.ProcessType;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProcessTypeRepository extends NameRepository<ProcessType, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.ProcessTypeDTO(
           s.id, s.name
           ) FROM ProcessType s
           """)
    List<ProcessTypeDTO> findAllDTO();
    Optional<ProcessType> findById(Integer id);

}

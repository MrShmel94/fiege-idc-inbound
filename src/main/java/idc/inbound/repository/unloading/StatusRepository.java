package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.StatusDTO;
import idc.inbound.entity.unloading.Status;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends NameRepository<Status, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.StatusDTO(
           s.id, s.name
           ) FROM Status s
           """)
    List<StatusDTO> findAllDTO();
    Optional<Status> findById(Integer id);

}

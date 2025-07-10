package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.SupplierTypeDTO;
import idc.inbound.dto.unloading.TypeErrorDTO;
import idc.inbound.entity.unloading.SupplierType;
import idc.inbound.entity.unloading.TypeError;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TypeErrorRepository extends NameRepository<TypeError, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.TypeErrorDTO(
           te.id, te.name
           ) FROM TypeError te
           """)
    List<TypeErrorDTO> findAllDTO();
    Optional<TypeError> findById(Integer id);
}

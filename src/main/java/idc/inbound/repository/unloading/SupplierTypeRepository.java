package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.SupplierTypeDTO;
import idc.inbound.entity.unloading.SupplierType;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SupplierTypeRepository extends NameRepository<SupplierType, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.SupplierTypeDTO(
           s.id, s.name
           ) FROM SupplierType s
           """)
    List<SupplierTypeDTO> findAllDTO();
    Optional<SupplierType> findById(Integer id);
}

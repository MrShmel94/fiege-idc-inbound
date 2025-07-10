package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.ProductTypeDTO;
import idc.inbound.entity.unloading.ProductType;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductTypeRepository extends NameRepository<ProductType, Integer> {
    @Query("""
           SELECT new idc.inbound.dto.unloading.ProductTypeDTO(
           s.id, s.name
           ) FROM ProductType s
           """)
    List<ProductTypeDTO> findAllDTO();
    Optional<ProductType> findById(Integer id);
}

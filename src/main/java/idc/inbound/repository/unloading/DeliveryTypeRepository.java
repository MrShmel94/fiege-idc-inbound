package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.DeliveryTypeDTO;
import idc.inbound.entity.unloading.DeliveryType;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeliveryTypeRepository extends NameRepository<DeliveryType, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.DeliveryTypeDTO(
           s.id, s.name
           ) FROM DeliveryType s
           """)
    List<DeliveryTypeDTO> findAllDTO();
    Optional<DeliveryType> findById(Integer id);
}

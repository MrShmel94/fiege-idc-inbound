package idc.inbound.repository.vision;

import idc.inbound.dto.unloading.DeliveryTypeDTO;
import idc.inbound.dto.vision.PositionDTO;
import idc.inbound.entity.unloading.DeliveryType;
import idc.inbound.entity.vision.Position;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends NameRepository<Position, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.vision.PositionDTO(
           p.id, p.name
           ) FROM Position p
           """)
    List<PositionDTO> findAllDTO();
    Optional<Position> findById(Integer id);
}

package idc.inbound.repository.vision;

import idc.inbound.dto.unloading.DeliveryTypeDTO;
import idc.inbound.dto.vision.DepartmentDTO;
import idc.inbound.entity.unloading.DeliveryType;
import idc.inbound.entity.vision.Department;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends NameRepository<Department, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.vision.DepartmentDTO(
           d.id, d.name
           ) FROM Department d
           """)
    List<DepartmentDTO> findAllDTO();
    Optional<Department> findById(Integer id);
}

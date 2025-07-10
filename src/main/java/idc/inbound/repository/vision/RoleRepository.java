package idc.inbound.repository.vision;

import idc.inbound.dto.vision.RoleDTO;
import idc.inbound.entity.vision.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("""
           SELECT new idc.inbound.dto.vision.RoleDTO(
           role.id, role.name, role.weight
           ) FROM Role role
           """)
    List<RoleDTO> getAllRoles();
}

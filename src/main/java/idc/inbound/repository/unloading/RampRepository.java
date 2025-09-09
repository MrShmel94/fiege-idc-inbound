package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.dto.unloading.RampDTO;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.Ramp;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RampRepository extends JpaRepository<Ramp, Long> {

    Optional<Ramp> findByName(String name);
    Optional<Ramp> findById(Integer id);

    @Query("""
           SELECT new idc.inbound.dto.unloading.RampDTO(
           r.id, r.name, CAST(r.status AS string), r.maxBuffer, r.actualBuffer
           ) FROM Ramp r
           WHERE r.status = :status
           """)
    List<RampDTO> findAllSelectStatusDTO(@Param("status") StatusBramAndRamp status);

    @Query("""
           SELECT new idc.inbound.dto.unloading.RampDTO(
           r.id, r.name, CAST(r.status AS string), r.maxBuffer, r.actualBuffer
           ) FROM Ramp r
           """)
    List<RampDTO> findAllDTO();

    @Query("""
           SELECT new idc.inbound.dto.unloading.RampDTO(
           r.id, r.name, CAST(r.status AS string), r.maxBuffer, r.actualBuffer
           ) FROM Ramp r
           WHERE r.id = :id
           """)
    Optional<RampDTO> findByIdDTO(@Param("id") Integer id);

    @Query("""
           SELECT new idc.inbound.dto.unloading.RampDTO(
           r.id, r.name, CAST(r.status AS string), r.maxBuffer, r.actualBuffer
           ) FROM Ramp r
           WHERE r.name = :name
           """)
    Optional<RampDTO> findByNameDTO(@Param("name") String name);

    @Query("""
           SELECT r.status FROM Ramp r WHERE r.id = :id
           """)
    String getStatusName(@Param("id") Integer id);

    @Query(value = """
            UPDATE unloading.ramp AS r
            SET status = :status
            WHERE id = :id
            RETURNING id, status, name, max_buffer, actual_buffer
    """, nativeQuery = true)
    Object setStatusToBram(@Param("id") Integer id,
                             @Param("status") String status);

}

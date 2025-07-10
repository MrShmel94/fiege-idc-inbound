package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BramRepository extends JpaRepository<Bram, Long> {

    Optional<Bram> findByName(String name);
    Optional<Bram> findById(Integer id);

    @Query("""
           SELECT new idc.inbound.dto.unloading.BramDTO(
           b.id, b.name, CAST(b.status AS string), b.maxBuffer, b.actualBuffer
           ) FROM Bram b
           WHERE b.status = :status
           """)
    List<BramDTO> findAllSelectStatusDTO(@Param("status") StatusBramAndRamp status);

    @Query("""
           SELECT new idc.inbound.dto.unloading.BramDTO(
           b.id, b.name, CAST(b.status AS string), b.maxBuffer, b.actualBuffer
           ) FROM Bram b
           """)
    List<BramDTO> findAllDTO();

    @Query("""
           SELECT new idc.inbound.dto.unloading.BramDTO(
           b.id, b.name, CAST(b.status AS string), b.maxBuffer, b.actualBuffer
           ) FROM Bram b
           WHERE b.id = :id
           """)
    Optional<BramDTO> findByIdDTO(@Param("id") Integer id);

    @Query("""
           SELECT new idc.inbound.dto.unloading.BramDTO(
           b.id, b.name, CAST(b.status AS string), b.maxBuffer, b.actualBuffer
           ) FROM Bram b
           WHERE b.name = :name
           """)
    Optional<BramDTO> findByNameDTO(@Param("name") String name);
}

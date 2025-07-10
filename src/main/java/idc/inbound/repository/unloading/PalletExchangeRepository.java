package idc.inbound.repository.unloading;

import idc.inbound.dto.unloading.PalletExchangeDTO;
import idc.inbound.entity.unloading.PalletExchange;
import idc.inbound.repository.NameRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PalletExchangeRepository extends NameRepository<PalletExchange, Integer> {

    @Query("""
           SELECT new idc.inbound.dto.unloading.PalletExchangeDTO(
           s.id, s.name
           ) FROM PalletExchange s
           """)
    List<PalletExchangeDTO> findAllDTO();
    Optional<PalletExchange> findById(Integer id);
}

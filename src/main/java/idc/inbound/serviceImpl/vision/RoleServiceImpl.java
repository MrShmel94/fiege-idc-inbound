package idc.inbound.serviceImpl.vision;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.vision.RoleDTO;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.vision.RoleRepository;
import idc.inbound.service.vision.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RedisCacheService redisCacheService;


    @Override
    public List<RoleDTO> getAllRoles() {
        return redisCacheService.getFromCache("roles_idc", new TypeReference<List<RoleDTO>>() {}).orElseGet(() -> {
            List<RoleDTO> dto = roleRepository.getAllRoles();

            if(!dto.isEmpty()){
                redisCacheService.saveToCache("roles_idc", dto);
            }

            return dto;
        });
    }
}

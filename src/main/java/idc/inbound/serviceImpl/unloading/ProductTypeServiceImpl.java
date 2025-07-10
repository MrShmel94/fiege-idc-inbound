package idc.inbound.serviceImpl.unloading;

import com.fasterxml.jackson.core.type.TypeReference;
import idc.inbound.dto.unloading.ProcessTypeDTO;
import idc.inbound.dto.unloading.ProductTypeDTO;
import idc.inbound.entity.unloading.ProductType;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.unloading.ProductTypeRepository;
import idc.inbound.request.NameChangeRequest;
import idc.inbound.request.UnloadingChangeRequestModal;
import idc.inbound.request.UnloadingRequestModal;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.AbstractCrudServiceUnloading;
import idc.inbound.service.unloading.ProductTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductTypeServiceImpl extends AbstractCrudServiceUnloading<
        ProductType,
        ProductTypeDTO,
        ProductTypeRepository,
        UnloadingRequestModal,
        UnloadingChangeRequestModal
        > implements ProductTypeService {

    public ProductTypeServiceImpl(
            ProductTypeRepository repository,
            RedisCacheService redisCacheService,
            SecurityUtils securityUtils,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        super(repository, redisCacheService, securityUtils, simpMessagingTemplate);
    }

    @Override
    protected String getRedisKey() {
        return "product_type_idc";
    }

    @Override
    protected List<ProductTypeDTO> getAllDTOImpl() {
        return repository.findAllDTO();
    }

    @Override
    protected ProductType createNewEntity() {
        return new ProductType();
    }

    @Override
    protected void updateEntityFromRequest(ProductType entity, NameChangeRequest request) {
        entity.setName(request.getNewName());
    }

    @Override
    protected void setUserId(ProductType entity, Integer userId) {
        entity.setUserId(userId);
    }

    @Override
    protected TypeReference<List<ProductTypeDTO>> getTypeReference() {
        return new TypeReference<List<ProductTypeDTO>>() {};
    }

    @Override
    protected String getConfigType() {
        return "productType";
    }

    @Override
    protected ProductTypeDTO mapToDto(ProductType productType) {
        return ProductTypeDTO.builder()
                .id(productType.getId())
                .name(productType.getName())
                .build();
    }
}

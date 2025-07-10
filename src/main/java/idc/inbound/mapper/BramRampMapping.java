package idc.inbound.mapper;

import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.dto.unloading.RampDTO;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.Ramp;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BramRampMapping {

    BramRampMapping INSTANCE = Mappers.getMapper(BramRampMapping.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    BramDTO toBramDTO(Bram bram);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    RampDTO toRampDTO(Ramp ramp);

    @Named("statusToString")
    default String map(StatusBramAndRamp value) {
        return value == null ? null : value.name();
    }
}

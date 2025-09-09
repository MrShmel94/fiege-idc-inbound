package idc.inbound.mapper;

import idc.inbound.dto.unloading.*;
import idc.inbound.entity.unloading.Bram;
import idc.inbound.entity.unloading.Ramp;
import idc.inbound.entity.unloading.StatusBramAndRamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapping {

    BookingMapping INSTANCE = Mappers.getMapper(BookingMapping.class);

    YardManDTO toYardManDTO(BookingDTO dto);
    ForkliftDTO toForkliftDTO(BookingDTO dto);
}

package idc.inbound.serviceImpl;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.dto.unloading.YardManDTO;
import idc.inbound.secure.aspect.AccessControl;
import idc.inbound.service.YardManService;
import idc.inbound.service.unloading.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class YardManServiceImpl implements YardManService {

    private final BookingService bookingService;

    @Override
    @AccessControl(
            minWeight = 70
    )
    public List<YardManDTO> getYardManData() {
        return bookingService.getBookingsByYardMan();
    }
}

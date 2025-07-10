package idc.inbound.serviceImpl;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.entity.unloading.StatusNotChanged;
import idc.inbound.service.ForkliftService;
import idc.inbound.service.unloading.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForkliftServiceImpl implements ForkliftService {

    private final BookingService bookingService;

    @Override
    public List<BookingDTO> getAllBookingCorrectType() {
        return bookingService.getBookingsByStatusName(StatusNotChanged.getStatusNotChanged());
    }
}

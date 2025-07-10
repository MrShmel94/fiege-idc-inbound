package idc.inbound.service;

import idc.inbound.dto.unloading.BookingDTO;

import java.util.List;

public interface ForkliftService {
    List<BookingDTO> getAllBookingCorrectType();
}

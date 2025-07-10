package idc.inbound.controller;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.service.ForkliftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/forklift")
@RequiredArgsConstructor
@Slf4j
public class ForkliftController {

    private final ForkliftService forkliftService;

    @GetMapping("/data")
    public ResponseEntity<List<BookingDTO>> getAllBookingsReady(){
        return ResponseEntity.ok(forkliftService.getAllBookingCorrectType());
    }
}

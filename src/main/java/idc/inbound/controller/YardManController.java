package idc.inbound.controller;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.dto.unloading.YardManDTO;
import idc.inbound.service.ForkliftService;
import idc.inbound.service.YardManService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/yard-man")
@RequiredArgsConstructor
@Slf4j
public class YardManController {

    private final YardManService yardManService;

    @GetMapping("/data")
    public ResponseEntity<List<YardManDTO>> getAllBookingsReady(){
        return ResponseEntity.ok(yardManService.getYardManData());
    }

}

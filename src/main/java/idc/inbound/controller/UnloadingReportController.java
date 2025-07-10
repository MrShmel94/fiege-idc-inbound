package idc.inbound.controller;

import idc.inbound.configuration.FileUploadConfig;
import idc.inbound.request.BookingAddRequest;
import idc.inbound.request.SignUpRequest;
import idc.inbound.service.unloading.UnloadingReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unloading_report")
@RequiredArgsConstructor
@Slf4j
public class UnloadingReportController {

    private final UnloadingReportService unloadingReportService;
    private final FileUploadConfig fileUploadConfig;

    @GetMapping("/getConfigFields")
    public ResponseEntity<?> getConfigUnloadingReport() {
        return ResponseEntity.ok(unloadingReportService.getConfig());
    }

    @GetMapping("/report/{date}")
    public ResponseEntity<?> getReportByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(unloadingReportService.getReportByDate(date));
    }

    @PostMapping("/uploadUnloadingReport")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("date") LocalDate date
    ) {
        if(file.getSize() > fileUploadConfig.getMaxFileSize()){
            return ResponseEntity.badRequest().body("File is too large");
        }

        List<String> result = unloadingReportService.uploadFileReport(file, date);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/add/{date}")
    public ResponseEntity<?> addReportByDate(@PathVariable LocalDate date, @Valid @RequestBody BookingAddRequest request) {
        unloadingReportService.uploadBooking(date, request);
        return ResponseEntity.ok().build();
    }
}

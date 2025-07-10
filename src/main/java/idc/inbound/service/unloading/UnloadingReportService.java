package idc.inbound.service.unloading;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.dto.unloading.BramDTO;
import idc.inbound.request.BookingAddRequest;
import idc.inbound.response.UnloadingReportResponse;
import idc.inbound.response.UserConfigResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface UnloadingReportService {
    UnloadingReportResponse getConfig();
    List<String> uploadFileReport(MultipartFile file, LocalDate date);
    List<BookingDTO> getReportByDate(LocalDate date);
    void uploadBooking(LocalDate date, BookingAddRequest request);
    UserConfigResponse getConfigByUser();
}

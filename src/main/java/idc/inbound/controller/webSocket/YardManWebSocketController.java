package idc.inbound.controller.webSocket;

import idc.inbound.request.BookingFieldUpdateRequest;
import idc.inbound.request.ForkliftControlUpdate;
import idc.inbound.request.YardManControlUpdate;
import idc.inbound.service.unloading.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class YardManWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingService bookingService;

    @MessageMapping("/yard-man/update")
    public void handleFieldYardManUpdate(@Payload YardManControlUpdate update, Principal principal) {
        try{
            bookingService.updateControlObjectYardMan(update, principal);
        }catch (Exception ex) {
            Map<String, Object> message = Map.of(
                    "type", "ERROR",
                    "message", ex.getMessage(),
                    "recordId", update.bookingId()
            );
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/any",
                    message
            );

            log.error(ex.getMessage());
        }
    }
}

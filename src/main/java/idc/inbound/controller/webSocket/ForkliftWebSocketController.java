package idc.inbound.controller.webSocket;

import idc.inbound.request.BookingFieldUpdateRequest;
import idc.inbound.service.unloading.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ForkliftWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingService bookingService;

    @MessageMapping("/forklift-socket/update")
    public void handleFieldUpdate(@DestinationVariable String date, @Payload BookingFieldUpdateRequest update, Principal principal) {
        log.info("Principal name: {}", principal != null ? principal.getName() : "null");

        try{
            bookingService.updateFieldPerForkLift(update, principal);
        }catch (Exception ex) {
            Map<String, Object> message = Map.of(
                    "type", "ERROR",
                    "message", ex.getMessage(),
                    "field", update.field(),
                    "recordId", update.recordId()
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

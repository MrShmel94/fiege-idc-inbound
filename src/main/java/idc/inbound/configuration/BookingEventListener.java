package idc.inbound.configuration;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.service.unloading.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final BookingService bookingService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        List<BookingDTO> dtos = bookingService.getBookingsByIds(event.bookingIds());
        Map<String, Object> message = Map.of(
                "type", event.type(),
                "data", dtos.size() == 1 ? dtos.getFirst() : dtos
        );
        String topic = "/topic/unloading-report/" + event.date();
        simpMessagingTemplate.convertAndSend(topic, message);
    }

    @EventListener
    public void handleBookingDeleted(BookingDeleteEvent event) {
        Map<String, Object> message = Map.of(
                "type", event.type(),
                "data", event.bookingIds()
        );
        String topic = "/topic/unloading-report/" + event.date();
        simpMessagingTemplate.convertAndSend(topic, message);
    }
}
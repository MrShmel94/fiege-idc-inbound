package idc.inbound.configuration;

import idc.inbound.dto.unloading.BookingDTO;
import idc.inbound.mapper.BookingMapping;
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
    public void handleBookingDeleted(BookingDeleteEvent event) {
        Map<String, Object> message = Map.of(
                "type", event.type(),
                "data", event.bookingIds()
        );
        event.topics().forEach(topic -> simpMessagingTemplate.convertAndSend(topic, message));
    }

    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        List<BookingDTO> dtos = bookingService.getBookingsByIds(event.bookingIds());

        event.topics().forEach(topic -> {
            switch(topic) {
                case "/topic/yard-man" -> {
                    simpMessagingTemplate.convertAndSend(topic, Map.of(
                            "type", event.type(),
                            "data", dtos.stream().map(BookingMapping.INSTANCE::toYardManDTO).toList()
                    ));
                }
                case "/topic/forklift-socket" -> {
                    simpMessagingTemplate.convertAndSend(topic, Map.of(
                            "type", event.type(),
                            "data", dtos.stream().map(BookingMapping.INSTANCE::toForkliftDTO).toList()
                    ));
                }
                default -> {
                    simpMessagingTemplate.convertAndSend(topic, Map.of(
                            "type", event.type(),
                            "data", dtos.size() == 1 ? dtos.getFirst() : dtos
                    ));
                }
            }
        });
    }
}
package idc.inbound.entity.unloading;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum StatusNotChanged {
    READY_TO_UNLOAD("Gotowe do rozładunku"),
    IN_PROGRESS("W trakcie rozładunku"),
    PAUSE("Pauza");

    private final String name;

    StatusNotChanged(String name) {
        this.name = name;
    }

    public static List<String> getStatusNotChanged() {
        return Stream.of(values())
                .map(StatusNotChanged::getName)
                .collect(Collectors.toList());
    }
}

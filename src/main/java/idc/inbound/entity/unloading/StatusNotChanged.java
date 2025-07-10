package idc.inbound.entity.unloading;

import java.util.List;

public enum StatusNotChanged {
    READY_TO_UNLOAD,
    IN_PROGRESS,
    PAUSE;

    public static List<String> getStatusNotChanged() {
        return List.of(READY_TO_UNLOAD.toString(), IN_PROGRESS.toString(), PAUSE.toString());
    }
}

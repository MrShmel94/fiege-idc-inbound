package idc.inbound.entity.unloading;

import idc.inbound.customError.NotFoundException;

public enum StatusBramAndRamp {
    ENABLED,
    DISABLED,
    OCCUPIED;

    public static StatusBramAndRamp fromStringOrThrow(String value) {
        try {
            return StatusBramAndRamp.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new NotFoundException("Status '" + value + "' is not valid!");
        }
    }
}

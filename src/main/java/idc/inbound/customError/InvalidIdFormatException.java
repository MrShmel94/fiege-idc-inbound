package idc.inbound.customError;

public class InvalidIdFormatException extends RuntimeException {
    public InvalidIdFormatException(String message) {
        super(message);
    }
}

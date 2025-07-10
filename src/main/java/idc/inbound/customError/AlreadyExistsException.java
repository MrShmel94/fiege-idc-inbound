package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends CustomException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class InvalidLoginRequestException extends CustomException {
    public InvalidLoginRequestException(String message, HttpStatus status) {
        super(message, status);
    }
}

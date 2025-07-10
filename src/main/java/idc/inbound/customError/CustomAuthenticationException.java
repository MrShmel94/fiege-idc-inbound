package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class CustomAuthenticationException extends CustomException {

    public CustomAuthenticationException(String message, HttpStatus status) {
        super(message, status);
    }

}

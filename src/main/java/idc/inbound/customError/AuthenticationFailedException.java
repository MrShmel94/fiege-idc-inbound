package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends CustomException{
    public AuthenticationFailedException(String errorMessage) {
        super(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}

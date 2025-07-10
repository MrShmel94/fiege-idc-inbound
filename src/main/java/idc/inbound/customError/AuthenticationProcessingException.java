package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class AuthenticationProcessingException extends CustomException  {

  public AuthenticationProcessingException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public AuthenticationProcessingException(String message, Throwable cause) {
    super(HttpStatus.BAD_REQUEST , message, cause);
  }

}

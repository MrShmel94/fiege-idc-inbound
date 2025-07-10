package idc.inbound.customError;

import org.springframework.http.HttpStatus;

public class CacheUpdateException extends CustomException {
    public CacheUpdateException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

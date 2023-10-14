package xyz.dvnlabs.corelib.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> resourceNotFound(ResourceNotFoundException exception, WebRequest request) {

        return new ResponseEntity<>(
                new ErrorDTO(
                        HttpStatus.NOT_FOUND.toString(),
                        exception.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false)
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<ErrorDTO> resourceExist(ResourceExistException exception, WebRequest request) {

        return new ResponseEntity<>(
                new ErrorDTO(
                        HttpStatus.CONFLICT.toString(),
                        exception.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false)
                ),
                HttpStatus.CONFLICT
        );
    }

}

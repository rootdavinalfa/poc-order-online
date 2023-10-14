package xyz.dvnlabs.gateway.handler;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> map = new java.util.HashMap<>(Map.of(
                "message", error.getMessage(),
                "errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "timeStamp", LocalDateTime.now()
        ));

        if (error instanceof ResourceNotFoundException) {
            map.replace("errorCode", HttpStatus.NOT_FOUND.value());
        } else if (error instanceof ResourceExistException) {
            map.replace("errorCode", HttpStatus.CONFLICT.value());
        }

        return map;
    }
}

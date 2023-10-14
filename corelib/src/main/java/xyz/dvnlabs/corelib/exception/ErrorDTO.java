package xyz.dvnlabs.corelib.exception;

import java.time.LocalDateTime;

public record ErrorDTO(
        String errorCode,
        String message,
        LocalDateTime timeStamp,
        String description
) {
}

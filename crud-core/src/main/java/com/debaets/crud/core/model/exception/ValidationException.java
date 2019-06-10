package com.debaets.crud.core.model.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ValidationException extends RuntimeException {

    private final String field;
    private final String errorCode;
    private final String reason;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param reason the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ValidationException(String field, String errorCode, String reason) {
        super(reason);
        this.field = field;
        this.errorCode = errorCode;
        this.reason = reason;
    }
}

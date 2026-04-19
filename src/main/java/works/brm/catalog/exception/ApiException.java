package works.brm.catalog.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ApiException notFound(String what)   { return new ApiException(HttpStatus.NOT_FOUND, what + " not found"); }
    public static ApiException conflict(String what)   { return new ApiException(HttpStatus.CONFLICT, what); }
    public static ApiException badRequest(String what) { return new ApiException(HttpStatus.BAD_REQUEST, what); }
}

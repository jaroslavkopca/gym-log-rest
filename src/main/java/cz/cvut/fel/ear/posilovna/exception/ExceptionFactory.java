package cz.cvut.fel.ear.posilovna.exception;

/**
 * ExceptionFactory interface for creating application-specific exceptions.
 */
public interface ExceptionFactory {
    BaseException createException(String message);

    BaseException createException(String message, Throwable cause);
}


package cz.cvut.fel.ear.posilovna.exception;
/**
 * Signifies that invalid data have been provided to the application.
 */
public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message);
    }
}
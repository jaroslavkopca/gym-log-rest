package cz.cvut.fel.ear.posilovna.exception;
public class PersistenceException extends BaseException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}


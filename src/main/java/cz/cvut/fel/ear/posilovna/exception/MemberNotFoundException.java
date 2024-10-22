package cz.cvut.fel.ear.posilovna.exception;

/**
 * Indicates that an error occurred when trying to access or work with a user's cart.
 */
public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException(String message) {
        super(message);
    }
}

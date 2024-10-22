package cz.cvut.fel.ear.posilovna.util;

import cz.cvut.fel.ear.posilovna.model.Role;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.CLIENT;

    /**
     * Username login form parameter.
     */
    public static final String USERNAME_PARAM = "username";

    private Constants() {
        throw new AssertionError();
    }
}

package cz.cvut.fel.ear.posilovna.model;


// moznost postavit signleton
public enum Role {
//    ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), GUEST("ROLE_GUEST");
    CLIENT("ROLE_CLIENT"),
    MEMBER("ROLE_MEMBER"),
    MEMBER_WITH_MEMBERSHIP("ROLE_MEMBER_WITH_MEMBERSHIP"),
    ADMIN("ROLE_ADMIN");
    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

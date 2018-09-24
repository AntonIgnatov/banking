package net.bigmir.venzor.enums;

public enum UserRole {
    BANK, USER;

    @Override
    public String toString() {
        return name();
    }
}

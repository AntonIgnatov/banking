package net.bigmir.venzor.enums;

public enum TransactionStatus {
    CANCELED, UNCONFIRMED, OK;

    @Override
    public String toString() {
        return name();
    }
}

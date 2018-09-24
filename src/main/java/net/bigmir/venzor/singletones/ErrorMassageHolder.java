package net.bigmir.venzor.singletones;

import net.bigmir.venzor.simpleclass.ErrorMessage;

import java.util.HashMap;
import java.util.Map;

public class ErrorMassageHolder {


    private static volatile ErrorMassageHolder instance = new ErrorMassageHolder();
    private volatile Map<Long, ErrorMessage> messages = new HashMap<>();

    private ErrorMassageHolder() {
    }

    public static ErrorMassageHolder getInstance() {
        return instance;
    }

    public synchronized void put(long userId, String str) {
        this.messages.put(userId, new ErrorMessage(str));
    }

    public synchronized ErrorMessage getMsg(long userId) {
        ErrorMessage msg = this.messages.get(userId);
        this.messages.remove(userId);
        return msg;
    }

}

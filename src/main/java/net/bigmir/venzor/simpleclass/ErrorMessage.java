package net.bigmir.venzor.simpleclass;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorMessage {
    private String msg;

    public ErrorMessage(String msg) {
        this.msg = msg;
    }
}

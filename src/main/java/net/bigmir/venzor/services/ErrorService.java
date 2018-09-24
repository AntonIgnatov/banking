package net.bigmir.venzor.services;

import net.bigmir.venzor.singletones.ErrorMassageHolder;
import net.bigmir.venzor.simpleclass.ErrorMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorService {
    private ErrorMassageHolder holder;

    public ErrorService() {
        this.holder = ErrorMassageHolder.getInstance();
    }

    public  void putErrorMsg(long userId, String str){
        this.holder.put(userId, str);
    }

    public  void putErrorMsgAndRedirect(long userId, String str, HttpServletResponse response){
        this.holder.put(userId, str);
        redirectToError(response);
    }

    public ErrorMessage getMsg(long userId){
        return this.holder.getMsg(userId);
    }

    public void redirectToError(HttpServletResponse response){
        try {
            response.sendRedirect("/error.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

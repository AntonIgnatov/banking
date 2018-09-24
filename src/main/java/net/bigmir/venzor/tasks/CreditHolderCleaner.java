package net.bigmir.venzor.tasks;

import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.singletones.CreditHolder;

import java.util.*;

public class CreditHolderCleaner extends TimerTask {


    public CreditHolderCleaner() {
    }


    @Override
    public void run() {
        long currentTime = new Date().getTime();
        List<String> codesToDelete = new LinkedList<>();
        CreditHolder holder = CreditHolder.getInstance();
        for (Map.Entry<String, CreditAccount> entry : holder.getMap()){
            if(currentTime - entry.getValue().getCreated().getTime() > 1*60*60*1000L){
                codesToDelete.add(entry.getKey());
            }
        }
        if(codesToDelete.size()!=0){
            holder.removeAll(codesToDelete);
        }

    }
}

package net.bigmir.venzor.singletones;

import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.CreditAccount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreditHolder {
    private static volatile CreditHolder instance = new CreditHolder();
    private volatile Map<String, CreditAccount> credits = new HashMap<>();

    private CreditHolder() {
    }

    public static CreditHolder getInstance() {
        return instance;
    }

    public synchronized void put(String code, CreditAccount credit) {
        this.credits.put(code, credit);
    }

    public synchronized CreditAccount getCredit(String code) {
        CreditAccount credit = this.credits.get(code);
        this.credits.remove(code);
        return credit;
    }

    public boolean checkUser(String code, SimpleUser user){
        if(!this.credits.containsKey(code)){
            return false;
        } else {
            return this.credits.get(code).getUser().equals(user);
        }
    }

    public Set<Map.Entry<String, CreditAccount>> getMap(){
        return credits.entrySet();
    }

    public synchronized void removeAll(List<String> codeList){
        for(String code : codeList){
            this.credits.remove(code);
        }
    }

}

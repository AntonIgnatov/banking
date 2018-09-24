package net.bigmir.venzor.dto;

import lombok.Data;

import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.entities.accounts.DebitAccount;


import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

@Data
public class AccountDTO implements Comparable<AccountDTO> {
    private Long id;
    private String user;
    private String currency;
    private String amount;
    private List<Long> cards;


    public AccountDTO(DebitAccount account){
        this.id=account.getId();
        this.user=account.getUser().getSurname()+" "+account.getUser().getName();
        this.currency=account.getCurrency();
        Formatter formatter = new Formatter();
        formatter.format("%.2f", account.getAmount());
        this.amount = formatter.toString();
        formatter.close();
        this.cards=new ArrayList<>();
        for(Card card : account.getCards()){
            this.cards.add(card.getId());
        }


    }

    @Override
    public int compareTo(AccountDTO o) {
        return this.getId().compareTo(o.getId());
    }
}

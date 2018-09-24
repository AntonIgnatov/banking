package net.bigmir.venzor.dto;

import lombok.Data;

import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.entities.accounts.CreditAccount;


import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDTO implements Comparable<CreditDTO> {
    private Long id;
    private String user;
    private String currency;
    private double amount;
    private double amountLimit;
    private int durability;
    private double mounthPay;
    private List<Long> cards;

    public CreditDTO(CreditAccount credit){
        this.id=credit.getId();
        this.user=credit.getUser().getSurname()+" "+credit.getUser().getName();
        this.currency=credit.getCurrency();
        this.cards=new ArrayList<>();
        for(Card card : credit.getCards()){
            this.cards.add(card.getId());
        }
        this.amount =credit.getAmount();
        this.amountLimit = credit.getAmountLimit();
        this.durability=credit.getDurability();
        this.mounthPay = credit.getMounthPay();
    }

    @Override
    public int compareTo(CreditDTO o) {
        return this.getId().compareTo(o.getId());
    }
}

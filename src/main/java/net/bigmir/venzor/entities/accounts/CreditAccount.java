package net.bigmir.venzor.entities.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.cards.CreditCard;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "credit_accaunts")
@Data
@NoArgsConstructor
public class CreditAccount extends BasicAccount<CreditCard> {
    private double amountLimit;
    private int durability;
    private double mounthPay;
    private Date created;
    private int counter;


    public CreditAccount(long id, SimpleUser user, String currency, double amountLimit, int durability, double mounthPay) {
        super(id, user, currency);
        this.amountLimit = amountLimit;
        super.setAmount(amountLimit);
        this.durability = durability;
        this.mounthPay = mounthPay;
        this.created = new Date();
        this.counter = 0;
    }

    @Override
    public void addCard(CreditCard card) {
       super.getCards().add(card);
    }

    public void count(){
        this.counter++;
    }




}

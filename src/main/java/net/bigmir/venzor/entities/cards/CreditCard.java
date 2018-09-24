package net.bigmir.venzor.entities.cards;

import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.accounts.CreditAccount;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "credit_cards")

@NoArgsConstructor
public class CreditCard extends Card<CreditAccount> {

    public CreditCard(CreditAccount account) {
        super(account);
    }
}

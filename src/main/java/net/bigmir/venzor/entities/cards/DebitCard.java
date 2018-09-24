package net.bigmir.venzor.entities.cards;

import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.accounts.DebitAccount;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "debit_cards")

@NoArgsConstructor
public class DebitCard extends Card<DebitAccount> {

    public DebitCard(DebitAccount account) {
        super(account);
    }
}

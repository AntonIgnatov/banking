package net.bigmir.venzor.entities.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.cards.DebitCard;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "debit_accounts")
@Data
@NoArgsConstructor
public class DebitAccount extends BasicAccount<DebitCard> {

    public DebitAccount(long id, SimpleUser user, String currency) {
        super(id, user, currency);
    }

    @Override
    public void addCard(DebitCard card){
        super.getCards().add(card);
    }


}

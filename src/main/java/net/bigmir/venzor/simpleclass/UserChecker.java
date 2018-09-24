package net.bigmir.venzor.simpleclass;

import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.exeptions.WrongAccountExeption;


public class UserChecker {

    public static <T extends BasicAccount> void checkUserBuAccount(T account, SimpleUser user) throws WrongAccountExeption {
        if (account.getUser().getId() != user.getId()) {
            throw new WrongAccountExeption();
        }
    }

    public static <T extends Card> void checkUserBuCard(T card, SimpleUser user) throws WrongAccountExeption {
        if (card.getAccount().getUser().getId() != user.getId()) {
            throw new WrongAccountExeption();
        }
    }
}

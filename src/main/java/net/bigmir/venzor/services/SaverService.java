package net.bigmir.venzor.services;

import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.entities.cards.CreditCard;
import net.bigmir.venzor.entities.cards.DebitCard;
import net.bigmir.venzor.repositories.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
public class SaverService {
    private DebitAccountRepository debitAccountRepository;
    private CreditAccountRepositiry creditAccountRepositiry;
    private DebitCardRepository debitCardRepository;
    private CreditCardRepository creditCardRepository;
    private UserRepository userRepository;

    public SaverService(DebitAccountRepository debitAccountRepository,
                        CreditAccountRepositiry creditAccountRepositiry,
                        DebitCardRepository debitCardRepository,
                        CreditCardRepository creditCardRepository,
                        UserRepository userRepository) {
        this.debitAccountRepository = debitAccountRepository;
        this.creditAccountRepositiry = creditAccountRepositiry;
        this.debitCardRepository = debitCardRepository;
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public <T extends BasicAccount> void saveAccount(T account) {
        if (DebitAccount.class.isAssignableFrom(account.getClass())) {
            debitAccountRepository.save((DebitAccount) account);
        } else {
            creditAccountRepositiry.save((CreditAccount) account);
        }
    }

    @Transactional
    public <T extends BasicAccount> void saveAccounts(List<T> accounts) {
        if (accounts.size() == 0) {
            return;
        }
        if (accounts.get(0).getClass().equals(DebitAccount.class)) {
            debitAccountRepository.saveAll((List<DebitAccount>) accounts);
        } else {
            creditAccountRepositiry.saveAll((List<CreditAccount>) accounts);
        }
    }

    @Transactional
    public <T extends Card> void saveCard(T card) {
        if (DebitCard.class.isAssignableFrom(card.getClass())) {
            debitCardRepository.save((DebitCard) card);
        } else {
            creditCardRepository.save((CreditCard) card);
        }
    }





    @Transactional
    public void saveUser(SimpleUser user) {
        userRepository.save(user);
    }

    public boolean checkUserExistingForRegistration(SimpleUser user) {
        return !userRepository.existsByPhoneIs(user.getPhone()) &&
                !userRepository.existsByIpnIs(user.getIpn()) &&
                !userRepository.existsByEmailIs(user.getEmail());
    }

    public boolean checkUserExistingForUpdate(String phone, String email) {
        return userRepository.existsByPhoneIs(phone) || userRepository.existsByEmailIs(email);
    }


}

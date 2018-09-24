package net.bigmir.venzor.services;

import net.bigmir.venzor.entities.Currency;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.entities.cards.CreditCard;
import net.bigmir.venzor.entities.cards.DebitCard;
import net.bigmir.venzor.enums.UserRole;
import net.bigmir.venzor.repositories.CurrencyRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CreationService {
    private AtomicLong accId = new AtomicLong(1000000000000000L);
    private CurrencyRepository currencyRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public CreationService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(4);
    }


    public SimpleUser createUser(String name, String surname, String addres, String phone, String ipn, String password, String email) {
        String passHash = passwordEncoder.encode(password);
        SimpleUser newUser = new SimpleUser(name, surname, addres, phone, ipn, passHash, email);
        return newUser;
    }

    public String passwordGeneration(String password){
        return passwordEncoder.encode(password);
    }


    public DebitAccount createDebitAccount(SimpleUser user, String currency) {
        DebitAccount account = new DebitAccount(accId.getAndIncrement(), user, currency);
        return account;
    }


    public CreditAccount createCreditAccount(SimpleUser user, String currency, double amountLimit, int durability, double monthPay) {
        CreditAccount account = new CreditAccount(accId.getAndIncrement(), user, currency, amountLimit, durability, monthPay);
        return account;
    }


    public DebitCard createDebitCard(DebitAccount account){
        DebitCard card = new DebitCard(account);
        return card;
    }


    public CreditCard createCreditCard(CreditAccount account){
        CreditCard card = new CreditCard(account);
        return card;
    }


    public SimpleUser addUserForInitialize() {
        SimpleUser bankUser = createUser("bank", "bank", "bank", "bank", "bank", "bank", "bank");
        bankUser.setRole(UserRole.BANK);
        for (Currency cur : currencyRepository.findAll()) {
            DebitAccount account = new DebitAccount(accId.getAndIncrement(), bankUser, cur.getName());
            account.setAmount(1000000);
            bankUser.addDebitAccount(account);
        }
        return bankUser;
    }
}

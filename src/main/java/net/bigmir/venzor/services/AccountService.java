package net.bigmir.venzor.services;

import net.bigmir.venzor.dto.AccountDTO;
import net.bigmir.venzor.dto.CreditDTO;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.exeptions.NotEnoughMoneyExeption;
import net.bigmir.venzor.exeptions.WrongAccountExeption;
import net.bigmir.venzor.repositories.CreditAccountRepositiry;
import net.bigmir.venzor.repositories.DebitAccountRepository;
import net.bigmir.venzor.repositories.UserRepository;
import net.bigmir.venzor.simpleclass.SMSSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AccountService {
    private DebitAccountRepository debitAccountRepository;
    private CreditAccountRepositiry creditAccountRepositiry;
    private UserRepository userRepository;
    private double comission = 0.05;

    public AccountService(DebitAccountRepository debitAccountRepository,
                          CreditAccountRepositiry creditAccountRepositiry,
                          UserRepository userRepository) {
        this.debitAccountRepository = debitAccountRepository;
        this.creditAccountRepositiry = creditAccountRepositiry;
        this.userRepository = userRepository;
    }

    public List<AccountDTO> getAllAccountDTO(long userId) {
        List<DebitAccount> accounts = debitAccountRepository.findDebitAccountsByUserId(userId);
        List<AccountDTO> dtoList = new ArrayList<>();
        for (DebitAccount acc : accounts) {
            dtoList.add(new AccountDTO(acc));
        }
        Collections.sort(dtoList);
        return dtoList;
    }


    public List<CreditDTO> getAllCreditsDTO(long userId) {
        List<CreditAccount> credits = creditAccountRepositiry.findCreditAccountsByUserId(userId);
        List<CreditDTO> dtoList = new ArrayList<>();
        for (CreditAccount cred : credits) {
            dtoList.add(new CreditDTO(cred));
        }
        Collections.sort(dtoList);
        return dtoList;
    }
    public List<CreditAccount> getAllCredits(){
        return creditAccountRepositiry.findAll();
    }


    public <T extends BasicAccount> T getAccount(long id) throws WrongAccountExeption {
        if (debitAccountRepository.existsById(id)) {
            return (T) debitAccountRepository.getOne(id);
        } else if (creditAccountRepositiry.existsById(id)) {
            return (T) creditAccountRepositiry.getOne(id);
        } else {
            throw new WrongAccountExeption();
        }
    }

    public <T extends BasicAccount> boolean checkAccountExisting(long id) {
        return debitAccountRepository.existsById(id) || creditAccountRepositiry.existsById(id);
    }

    public <T extends BasicAccount> void checkAmountWithComission(T account, double sumToCheck) throws NotEnoughMoneyExeption {
        if (account.getAmount() < (sumToCheck * (1 + comission))) {
            throw new NotEnoughMoneyExeption();
        }
    }

    public void checkAmount(DebitAccount account, double sumToCheck) throws NotEnoughMoneyExeption {
        if (account.getAmount() < (sumToCheck * (1 + comission))) {
            throw new NotEnoughMoneyExeption();
        }
    }

    @Transactional
    public synchronized <T extends BasicAccount> T addMoney(T account, double sumToAdd) {
        account.increaseAmount(sumToAdd);
        return account;
    }

    @Transactional
    public synchronized <T extends BasicAccount> T removeMoneyWithComission(T account, double sumToremove) throws NotEnoughMoneyExeption {
        checkAmountWithComission(account, sumToremove);
        account.descreaseAmount(sumToremove * (1 + comission));
        return account;
    }

    @Transactional
    public synchronized <T extends BasicAccount> T removeMoney(T account, double sumToremove) throws NotEnoughMoneyExeption {
        try {
            checkAmountWithComission(account, sumToremove);
        } catch (NotEnoughMoneyExeption e) {
            account.descreaseAmount(sumToremove);
            throw e;
        }
        account.descreaseAmount(sumToremove);
        return account;
    }


    public double getComission(double amount) {
        return amount * comission;
    }

    public DebitAccount getBankAccount(String currensy) {
        return userRepository.findByPhoneIs("bank").getDebitAccountByCurrency(currensy);
    }

    @Transactional
    public DebitAccount bankComision(double amount, String currency) {
        DebitAccount bank = getBankAccount(currency);
        bank.increaseAmount(getComission(amount));
        return bank;
    }

    @Transactional
    public void creditPay() {
        for (CreditAccount credit : getAllCredits()) {
            if (credit.getDurability() == credit.getCounter() || (credit.getCreated().getTime() - 2592000000L) < 0) {
                continue;
            }
            try {
                credit.count();
                removeMoney(credit, credit.getMounthPay());
            } catch (NotEnoughMoneyExeption e) {
                try {
                    SMSSender.sendSMSmsg(credit.getUser().getPhone(), "Prostochen credit!");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            DebitAccount bank = getBankAccount(credit.getCurrency());
            addMoney(bank, credit.getMounthPay());
        }
    }

}

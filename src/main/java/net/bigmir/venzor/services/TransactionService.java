package net.bigmir.venzor.services;

import net.bigmir.venzor.dto.TransactionDTO;
import net.bigmir.venzor.entities.ConfirmSMSTransaction;
import net.bigmir.venzor.entities.SimpleTransaction;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.enums.TransactionStatus;
import net.bigmir.venzor.exeptions.ConfirmTimeoutExeption;
import net.bigmir.venzor.exeptions.NotEnoughMoneyExeption;
import net.bigmir.venzor.exeptions.WrongAccountExeption;
import net.bigmir.venzor.exeptions.WrongCodeExeption;
import net.bigmir.venzor.repositories.CurrencyRepository;
import net.bigmir.venzor.repositories.SMSTransactionRepository;
import net.bigmir.venzor.repositories.TransactionsRepositiry;
import net.bigmir.venzor.simpleclass.SMSSender;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionService {
    private TransactionsRepositiry transactionsRepositiry;
    private SMSTransactionRepository smsTransactionRepository;
    private AccountService accountService;
    private CurrencyRepository currencyRepository;
    private CardService cardService;

    public TransactionService(TransactionsRepositiry transactionsRepositiry,
                              SMSTransactionRepository smsTransactionRepository,
                              AccountService accountService,
                              CurrencyRepository currencyRepository,
                              CardService cardService) {
        this.transactionsRepositiry = transactionsRepositiry;
        this.smsTransactionRepository = smsTransactionRepository;
        this.accountService = accountService;
        this.currencyRepository = currencyRepository;
        this.cardService = cardService;
    }

    public List<TransactionDTO> transactionsForAccount(long id) {
        return transactionToDTO(transactionsRepositiry.findByAccountFromIs(id),
                transactionsRepositiry.findByAccountToIs(id),
                true);
    }

    public List<TransactionDTO> transactionsForCard(long id) {
        return transactionToDTO(transactionsRepositiry.findByCardFromIs(id),
                transactionsRepositiry.findByCardToIs(id),
                false);
    }


    //f=true - for account, false - for card
    public List<TransactionDTO> transactionToDTO(List<SimpleTransaction> transactionFromList, List<SimpleTransaction> transactionToList, boolean f) {
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        for (SimpleTransaction transaction : transactionFromList) {
            if (f) {
                transactionDTOList.add(new TransactionDTO().forAccount(transaction).from(transaction));
            } else {
                transactionDTOList.add(new TransactionDTO().forCard(transaction).from(transaction));
            }
        }
        for (SimpleTransaction transaction : transactionToList) {
            if (f) {
                transactionDTOList.add(new TransactionDTO().forAccount(transaction).to(transaction));
            } else {
                transactionDTOList.add(new TransactionDTO().forCard(transaction).to(transaction));
            }
        }
        Collections.sort(transactionDTOList);
        return transactionDTOList;
    }

    public long getAccountFromId(long transactionId) {
        return transactionsRepositiry.getOne(transactionId).getAccountFrom();
    }

    public long getAccountToId(long transactionId) {
        return transactionsRepositiry.getOne(transactionId).getAccountTo();
    }

    @Transactional
    public void saveTransactions(List<SimpleTransaction> transactions) {
        transactionsRepositiry.saveAll(transactions);
    }

    public List<SimpleTransaction> findUnconfirmed() {
        return transactionsRepositiry.findByStatus(TransactionStatus.UNCONFIRMED);
    }

    public SimpleTransaction getTransaction(long id) {
        return transactionsRepositiry.getOne(id);
    }

    @Transactional
    public ConfirmSMSTransaction createSMSforTransaction(SimpleTransaction tr) {
        int code = 0;
        while (code < 1000) {
            code = (int) (Math.random() * 10000);
        }
        code = 1111;
        ConfirmSMSTransaction sms = new ConfirmSMSTransaction(code);
        sms.setTransaction(tr);
        boolean check = true;
        while (check) {
            if(!smsTransactionRepository.existsByCode(code)){
                smsTransactionRepository.save(sms);
                check = false;
            } else{
                code = 0;
                while (code < 1000) {
                    code = (int) (Math.random() * 10000);
                }
                sms.setCode(code);
            }
        }
        return sms;
    }

    @Transactional
    public <T extends BasicAccount> void createTransaction(long cardFrom, long cardTo, T accFrom, long accTo, double amount, String description) {
        SimpleTransaction transaction = new SimpleTransaction(cardFrom, accFrom, cardTo, accTo, amount);
        if (description.length() > 1) {
            transaction.setDescription(description);
        }
        transactionsRepositiry.save(transaction);
        ConfirmSMSTransaction sms = createSMSforTransaction(transaction);

        try {
            SMSSender.sendSMScode(accFrom.getUser().getPhone(), sms.getCode());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void confirmTransaction(SimpleTransaction tr) throws NotEnoughMoneyExeption {
        boolean accountExisting = accountService.checkAccountExisting(tr.getAccountTo());
        if (accountExisting) {
            transferInsideBank(tr);
        } else {
            transferBetweenBanks(tr);
        }
    }

    @Transactional
    public <T extends BasicAccount> void transferBetweenBanks(SimpleTransaction tr) throws NotEnoughMoneyExeption {
        double amount = tr.getAmountFrom();
        try {
            T accountFrom = accountService.getAccount(tr.getAccountFrom());
            accountService.checkAmountWithComission(accountFrom, amount);
            accountService.removeMoneyWithComission(accountFrom, amount);

            // here must be API for connection with other bank

            tr.setStatus(TransactionStatus.OK);
            transactionsRepositiry.save(tr);
            createComission(accountFrom, amount);
        } catch (WrongAccountExeption e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public <T extends BasicAccount> void transferInsideBank(SimpleTransaction tr) throws NotEnoughMoneyExeption {
        double amountFrom = tr.getAmountFrom();
        try {
            T accountFrom = accountService.getAccount(tr.getAccountFrom());
            accountService.checkAmountWithComission(accountFrom, amountFrom);
            T accountTo = accountService.getAccount(tr.getAccountTo());
            accountService.removeMoneyWithComission(accountFrom, amountFrom);
            double amountTo = tr.getAmountFrom();
            if (checkingTheNeedForAnExchange(accountFrom, accountTo)) {
                amountTo = currencyExchange(accountFrom, accountTo, amountFrom);
            }
            tr.setAmountTo(amountTo);
            tr.setCurrencyTo(accountTo.getCurrency());
            tr.setStatus(TransactionStatus.OK);
            transactionsRepositiry.save(tr);
            createComission(accountFrom, amountFrom);
            accountService.addMoney(accountTo, amountTo);
        } catch (WrongAccountExeption e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public <T extends BasicAccount> void createComission(T accountFrom, double amount) {
        DebitAccount bank = accountService.bankComision(amount, accountFrom.getCurrency());
        SimpleTransaction comission = new SimpleTransaction(
                accountFrom,
                bank,
                accountService.getComission(amount));
        transactionsRepositiry.save(comission);
    }

    @Transactional
    public <T extends BasicAccount> void createAdjuctionReport(T account, long cardId, double amount) {
        SimpleTransaction tr = new SimpleTransaction(account, cardId, amount);
        tr.setStatus(TransactionStatus.OK);
        transactionsRepositiry.save(tr);
    }


    public <T extends BasicAccount> boolean checkingTheNeedForAnExchange(T accountFrom, T accountTo) {
        String currencyFrom = accountFrom.getCurrency();
        String currencyTo = accountTo.getCurrency();
        return !currencyFrom.equals(currencyTo);
    }

    public <T extends BasicAccount> double currencyExchange(T accountFrom, T accountTo, double amount) {
        double rateFrom = currencyRepository.getOne(accountFrom.getCurrency()).getRateBuy();
        double rateTo = currencyRepository.getOne(accountTo.getCurrency()).getRateSale();
        DebitAccount bank = accountService.getBankAccount(accountFrom.getCurrency());
        accountService.addMoney(bank, amount * 0.02);
        return amount * rateFrom / rateTo;
    }

    public SimpleTransaction getTransactionByCode(int code) throws WrongCodeExeption, ConfirmTimeoutExeption {
        if (!smsTransactionRepository.existsByCode(code)) {
            throw new WrongCodeExeption();
        }
        SimpleTransaction tr = smsTransactionRepository.findByCodeIs(code).getTransaction();
        if (tr.getStatus() == TransactionStatus.OK) {
            throw new ConfirmTimeoutExeption("Транзакція вже проведена");
        } else if (tr.getStatus() == TransactionStatus.CANCELED) {
            throw new ConfirmTimeoutExeption("Транзакція відмінена");
        } else {
            return tr;
        }

    }

    @Transactional
    public void deleteSMS(int code) {
        smsTransactionRepository.delete(smsTransactionRepository.findByCodeIs(code));
    }


}

package net.bigmir.venzor.controllers;

import net.bigmir.venzor.entities.BackMessage;
import net.bigmir.venzor.entities.SimpleTransaction;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.entities.cards.CreditCard;
import net.bigmir.venzor.entities.cards.DebitCard;
import net.bigmir.venzor.exeptions.ConfirmTimeoutExeption;
import net.bigmir.venzor.exeptions.NotEnoughMoneyExeption;
import net.bigmir.venzor.exeptions.WrongAccountExeption;
import net.bigmir.venzor.exeptions.WrongCodeExeption;
import net.bigmir.venzor.repositories.CommunicationsRepository;
import net.bigmir.venzor.services.*;
import net.bigmir.venzor.simpleclass.SMSSender;
import net.bigmir.venzor.simpleclass.UserChecker;
import net.bigmir.venzor.singletones.CardInfoHolder;
import net.bigmir.venzor.singletones.CreditHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class BankController {
    private UserService userService;
    private CreationService creationService;
    private CardService cardService;
    private TransactionService transactionService;
    private AccountService accountService;
    private SaverService saverService;
    private ErrorService errorService;
    private CommunicationsRepository communications;

    public BankController(UserService userService,
                          CreationService creationService,
                          CardService cardService,
                          TransactionService transactionService,
                          AccountService accountService,
                          SaverService saverService,
                          CommunicationsRepository communications) {
        this.userService = userService;
        this.creationService = creationService;
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.saverService = saverService;
        this.errorService = new ErrorService();
        this.communications = communications;
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login.html";
    }

    @GetMapping("/account")
    public String accountPage() {
        return "account.html";
    }


    @PostMapping("/regist")
    public void registerNewUser(@RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("address") String addres,
                                @RequestParam("phone") String phone,
                                @RequestParam("ipn") String ipn,
                                @RequestParam("password") String password,
                                @RequestParam("email") String email,
                                HttpServletResponse response) {

        SimpleUser user = creationService.createUser(name, surname, addres, phone, ipn, password, email);
        if (saverService.checkUserExistingForRegistration(user)) {
            saverService.saveUser(user);
        } else {
            response.setStatus(405);
        }
    }

    @PostMapping("/user/update")
    public void updateUser(@RequestParam(value = "address", required = false) String addres,
                           @RequestParam(value = "phone", required = false) String phone,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam(value = "email", required = false) String email,
                           HttpServletResponse response) {
        if (addres.length() == 0 && phone.length() == 0 && password.length() == 0 && email.length() == 0) {
            response.setStatus(200);
            return;
        }
        SimpleUser userToUpdate = userService.getCurrentUser(response);
        if (saverService.checkUserExistingForUpdate(phone, email)) {
            errorService.putErrorMsg(userToUpdate.getId(), "Користувач з дакими данними вже зареестрований");
            response.setStatus(405);
            return;
        }
        if (addres.length() > 0) {
            userToUpdate.setAddres(addres);
        }
        if (phone.length() > 0) {
            userToUpdate.setPhone(phone);
        }
        if (password.length() > 0) {
            password = creationService.passwordGeneration(password);
            userToUpdate.setPassword(password);
        }
        if (email.length() > 0) {
            userToUpdate.setEmail(email);
        }
        saverService.saveUser(userToUpdate);
        if ((phone != null || password != null) && (phone.length() > 1 || password.length() > 1)) {
            try {
                response.sendRedirect("/logout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @PostMapping("/accounts/create")
    public void createDebitAccount(@RequestParam("acc_new_amount") String amountStr,
                                   @RequestParam("acc_new_currency") String currency,
                                   HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            double amount = amountConvertion(amountStr);
            DebitAccount account = creationService.createDebitAccount(user, currency);
            account.increaseAmount(amount);
            saverService.saveAccount(account);
        } catch (NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірні данні");
            response.setStatus(405);
        }
    }

    @PostMapping("/credits/create")
    public void createCredit(@RequestParam("credit_amount") String amountLimitStr,
                             @RequestParam("credit_currency") String currency,
                             @RequestParam("credit_duration") int durability,
                             @RequestParam("credit_per_month") double mounthPay,
                             HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        DebitAccount bank = accountService.getBankAccount(currency);
        try {
            double amountLimit = amountConvertion(amountLimitStr);
            accountService.checkAmountWithComission(bank, amountLimit);
            CreditAccount credit = creationService.createCreditAccount(user, currency, amountLimit, durability, mounthPay);
            int code = (int) (Math.random() * 10000);
            SMSSender.sendSMScode(user.getPhone(), code);
            CreditHolder holder = CreditHolder.getInstance();
            holder.put(String.valueOf(code) + String.valueOf(user.getId()), credit);
        } catch (NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірні данні");
            response.setStatus(405);
        } catch (NotEnoughMoneyExeption e) {
            errorService.putErrorMsg(user.getId(), "Кредитування в цій валюті тимчасово неможливе");
            response.setStatus(405);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @PostMapping("/confirm/creation")
    public void confirmCreditCreation(@RequestParam("confirm_create_code") String codeStr,
                                      HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        int code;
        try {
            code = Integer.valueOf(codeStr);
            String codeKey = String.valueOf(code) + String.valueOf(user.getId());
            CreditHolder holder = CreditHolder.getInstance();
            if (!holder.checkUser(codeKey, user)) {
                throw new WrongCodeExeption();
            }
            CreditAccount credit = holder.getCredit(codeKey);
            DebitAccount bank = accountService.getBankAccount(credit.getCurrency());
            accountService.checkAmount(bank, credit.getAmountLimit());
            accountService.removeMoney(bank, credit.getAmountLimit());
            saverService.saveAccount(credit);
        } catch (NumberFormatException | WrongCodeExeption e) {
            errorService.putErrorMsg(user.getId(), "Невірний код");
            response.setStatus(405);
        } catch (NotEnoughMoneyExeption e) {
            errorService.putErrorMsg(user.getId(), "Кредитування в цій валюті тимчасово неможливе");
            response.setStatus(405);
        }


    }

    @PostMapping("/accounts/add_card")
    public void createDebitCard(@RequestParam("acc_new_card") String accountIdStr,
                                HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            long accountId = Long.valueOf(accountIdStr);
            DebitAccount account = accountService.getAccount(accountId);
            UserChecker.checkUserBuAccount(account, user);
            DebitCard card = creationService.createDebitCard(account);
            saverService.saveCard(card);
            CardInfoHolder holder = CardInfoHolder.getInstance();
            holder.put(user.getId(), card);
        } catch (WrongAccountExeption | NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірний номер рахунку");
            response.setStatus(405);
        }

    }

    @PostMapping("/credits/add_card")
    public void createCreditCard(@RequestParam("credit_new_card") String creditIdStr,
                                 HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            long creditId = Long.valueOf(creditIdStr);
            CreditAccount credit = accountService.getAccount(creditId);
            UserChecker.checkUserBuAccount(credit, user);
            CreditCard card = creationService.createCreditCard(credit);
            saverService.saveCard(card);
            CardInfoHolder holder = CardInfoHolder.getInstance();
            holder.put(user.getId(), card);
        } catch (WrongAccountExeption | NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірний номер рахунку");
            response.setStatus(405);
        }

    }

    @PostMapping("/card/adjunction")
    public <T extends Card> void addMoneyToCard(@RequestParam("card_adj_num") String cardIdStr,
                                                @RequestParam("card_adj_amount") String amountStr,
                                                HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            long cardId = Long.valueOf(cardIdStr);
            double amount = amountConvertion(amountStr);
            T card = cardService.getCard(cardId);
            UserChecker.checkUserBuCard(card, user);
            card.getAccount().increaseAmount(amount);
            saverService.saveAccount(card.getAccount());
            transactionService.createAdjuctionReport(card.getAccount(), cardId, amount);
        } catch (NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірні данні");
            response.setStatus(405);
        } catch (WrongAccountExeption e) {
            errorService.putErrorMsg(user.getId(), "Невірні данні картки");
            response.setStatus(405);
        }

    }

    @PostMapping("/account/adjunction")
    public <T extends BasicAccount> void addMoneyToAccount(@RequestParam("acc_adj_num") String accountIdStr,
                                                           @RequestParam("acc_adj_amount") String amountStr,
                                                           HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            long accountId = Long.valueOf(accountIdStr);
            double amount = amountConvertion(amountStr);
            T account = accountService.getAccount(accountId);
            UserChecker.checkUserBuAccount(account, user);
            account.increaseAmount(amount);
            saverService.saveAccount(account);
            transactionService.createAdjuctionReport(account, 0, amount);
        } catch (NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Невірні данні");
            response.setStatus(405);
        } catch (WrongAccountExeption e) {
            errorService.putErrorMsg(user.getId(), "Невірний номер рахунку");
            response.setStatus(405);
        }
    }

    @PostMapping("/transfer")
    public <T extends Card> void cardTransfer(@RequestParam("card_number") long cardFromId,
                                              @RequestParam("term_month") int month,
                                              @RequestParam("term_year") int year,
                                              @RequestParam("cvc") int cvv,
                                              @RequestParam("pay_amount") String amountStr,
                                              @RequestParam("to_card_number") String cardToIdStr,
                                              HttpServletResponse response) {
        SimpleUser currentUser = userService.getCurrentUser(response);
        try {
            long cardToId = Long.valueOf(cardToIdStr);
            T cardFrom = cardService.getExistingCard(cardFromId, month, year, cvv);
            UserChecker.checkUserBuCard(cardFrom, currentUser);
            double amount = amountConvertion(amountStr);
            accountService.checkAmountWithComission(cardFrom.getAccount(), amount);
            try {
                T cardTo = cardService.getCard(cardToId);
                transactionService.createTransaction(cardFromId, cardToId, cardFrom.getAccount(), cardTo.getAccount().getId(), amount, "");
            } catch (WrongAccountExeption e) {
                transactionService.createTransaction(cardFromId, cardToId, cardFrom.getAccount(), 0, amount, "");
            }
        } catch (WrongAccountExeption | NumberFormatException e) {
            errorService.putErrorMsg(currentUser.getId(), "Невірні данні");
            response.setStatus(405);
        } catch (NotEnoughMoneyExeption e) {
            errorService.putErrorMsg(currentUser.getId(), "Недостатньо коштів на рахунку");
            response.setStatus(405);
        }

    }

    @PostMapping("/transfer/external")
    public <T extends BasicAccount> void bankTransfer(@RequestParam("account") long accFromId,
                                                      @RequestParam("to_account") String accToIdStr,
                                                      @RequestParam("mfo") String mfo,
                                                      @RequestParam("bank") String bank,
                                                      @RequestParam("egrpou") String egrpou,
                                                      @RequestParam("transfer_sum") String amountStr,
                                                      @RequestParam("descr") String descr,
                                                      HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            double amount = amountConvertion(amountStr);
            long accToId = Long.valueOf(accToIdStr);
            T accountFrom = accountService.getAccount(accFromId);
            UserChecker.checkUserBuAccount(accountFrom, user);
            accountService.checkAmountWithComission(accountFrom, amount);
            String description = "Перевод от " + accFromId + " по реквизитам: " + mfo + " " + bank + " " + egrpou + " " + descr;
            transactionService.createTransaction(0, 0, accountFrom, accToId, amount, description);
        } catch (NumberFormatException e) {
            errorService.putErrorMsg(user.getId(), "Некоректні данні");
            response.setStatus(405);
        } catch (WrongAccountExeption e) {
            errorService.putErrorMsg(user.getId(), "Невірний номер рахунку");
            response.setStatus(405);
        } catch (NotEnoughMoneyExeption e) {
            errorService.putErrorMsg(user.getId(), "Недостатньо коштів на рахунку");
            response.setStatus(405);
        }
    }

    @PostMapping("/confirm/transaction")
    public <T extends BasicAccount> void confirmTransaction(@RequestParam("confirm_transfer_code") int code,
                                                            HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            SimpleTransaction tr = transactionService.getTransactionByCode(code);
            T account = accountService.getAccount(tr.getAccountFrom());
            UserChecker.checkUserBuAccount(account, user);
            transactionService.confirmTransaction(tr);
            transactionService.deleteSMS(code);
        } catch (WrongCodeExeption | WrongAccountExeption e) {
            errorService.putErrorMsg(user.getId(), "Невірний код");
            response.setStatus(405);
        } catch (ConfirmTimeoutExeption e) {
            errorService.putErrorMsg(user.getId(), e.getMessage());
            response.setStatus(405);
        } catch (NotEnoughMoneyExeption e) {
            errorService.putErrorMsg(user.getId(), "Недостатньо коштів на рахунку");
            response.setStatus(405);
        }
    }

    @PostMapping("/communications")
    public void communication(@RequestParam("email") String email, @RequestParam("message") String message){
        BackMessage msg = new BackMessage(email, message);
        communications.save(msg);
    }

    public double amountConvertion(String amountStr) throws NumberFormatException {
        amountStr = amountStr.replace(',', '.');
        double amount = Double.valueOf(amountStr);
        if (amountStr.lastIndexOf('.') - amountStr.length() > 3 || amount <= 0) {
            throw new NumberFormatException();
        }
        return amount;
    }


}

package net.bigmir.venzor.controllers;

import net.bigmir.venzor.dto.*;
import net.bigmir.venzor.entities.Currency;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.cards.Card;
import net.bigmir.venzor.simpleclass.ErrorMessage;
import net.bigmir.venzor.exeptions.WrongAccountExeption;
import net.bigmir.venzor.repositories.CurrencyRepository;
import net.bigmir.venzor.services.*;
import net.bigmir.venzor.singletones.CardInfoHolder;
import net.bigmir.venzor.simpleclass.UserChecker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
public class BankRestController {
    private AccountService accountService;
    private UserService userService;
    private TransactionService transactionService;
    private CurrencyRepository currencyRepository;
    private CardService cardService;
    private ErrorService errorService;

    public BankRestController(AccountService accountService,
                              UserService userService,
                              TransactionService transactionService,
                              CurrencyRepository currencyRepository,
                              CardService cardService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.currencyRepository = currencyRepository;
        this.cardService = cardService;
        this.errorService = new ErrorService();
    }

    @GetMapping("/accounts")
    public List<AccountDTO> getAllAcc(HttpServletResponse response) {
        long userId = userService.getCurrentUser(response).getId();
        return accountService.getAllAccountDTO(userId);
    }

    @GetMapping("/credits")
    public List<CreditDTO> getAllCredits(HttpServletResponse response) {
        long userId = userService.getCurrentUser(response).getId();
        return accountService.getAllCreditsDTO(userId);
    }

    @GetMapping({"/accounts/{id}", "/credits/{id}"})
    public <T extends BasicAccount> List<TransactionDTO> getTransactionsForAccount(@PathVariable("id") long id,
                                                                                  HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try {
            T account = accountService.getAccount(id);
            UserChecker.checkUserBuAccount(account, user);
            return transactionService.transactionsForAccount(id);
        } catch (WrongAccountExeption e) {
            errorService.putErrorMsgAndRedirect(user.getId(), "Невірний номер рахунку", response);
            response.setStatus(405);
            return null;
        }
    }

    @GetMapping("/user/details")
    public SimpleUserDTO userDetails(HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        return userService.getUserDTO(user);
    }

    @GetMapping("/accounts/cards/{card_id}")
    public <T extends Card> List<TransactionDTO> cardInfo(@PathVariable("card_id") long id,
                                                        HttpServletResponse response) {
        SimpleUser user = userService.getCurrentUser(response);
        try{
            T card = cardService.getCard(id);
            UserChecker.checkUserBuCard(card, user);
            return transactionService.transactionsForCard(id);
        } catch (WrongAccountExeption e){
            errorService.putErrorMsgAndRedirect(user.getId(), "Невірний номер рахунку", response);
            response.setStatus(405);
            return null;
        }
    }



    @GetMapping("/errormsg")
    public ErrorMessage getErrorMessage(HttpServletResponse response){
        SimpleUser user = userService.getCurrentUser(response);
        return errorService.getMsg(user.getId());
    }

    @GetMapping("/rates")
    public List getCyrrencyRates(HttpServletRequest request){
        String url = request.getHeader("referer");
        List<Currency> rateList = currencyRepository.findAll();
        if(url.charAt(url.length()-1)=='/' || url.contains("index.html")) {
            rateList.remove(currencyRepository.getOne("UAH"));
            Collections.sort(rateList);
            return rateList;
        }else {
            List<String> namesList = new LinkedList<>();
            for(Currency cur : rateList){
                namesList.add(cur.getName());
            }
            Collections.sort(namesList);
            return namesList;
        }
    }


    @GetMapping("/card/info")
    public CardDTO newCardInfo(HttpServletResponse response){
      long userId = userService.getCurrentUser(response).getId();
        CardInfoHolder holder = CardInfoHolder.getInstance();
        return holder.getInfo(userId);
    }


}

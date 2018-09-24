package net.bigmir.venzor;

import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.repositories.CurrencyRepository;
import net.bigmir.venzor.services.AccountService;
import net.bigmir.venzor.services.CreationService;
import net.bigmir.venzor.services.SaverService;
import net.bigmir.venzor.services.TransactionService;
import net.bigmir.venzor.simpleclass.CurrencyJson;
import net.bigmir.venzor.tasks.CreditHolderCleaner;
import net.bigmir.venzor.tasks.CreditPayment;
import net.bigmir.venzor.tasks.CurrencyFiller;
import net.bigmir.venzor.tasks.TransactionChecker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class DemoApplication {
    private CreationService creationService;
    private SaverService saverService;
    private TransactionService transactionService;
    private CurrencyRepository currencyRepository;
    private AccountService accountService;

    public DemoApplication(CreationService creationService,
                           SaverService saverService,
                           TransactionService transactionService,
                           CurrencyRepository currencyRepository,
                           AccountService accountService) {
        this.creationService = creationService;
        this.saverService = saverService;
        this.transactionService = transactionService;
        this.currencyRepository = currencyRepository;
        this.accountService = accountService;
    }

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);

    }

    @Bean
    public CommandLineRunner demo() {
        return new CommandLineRunner() {

            public void run(String... strings) {
                currencyRepository.saveAll(CurrencyJson.initCurrencies());
                TimerTask checker = new TransactionChecker(transactionService);
                TimerTask curRates = new CurrencyFiller(currencyRepository);
                TimerTask payment = new CreditPayment(accountService);
                TimerTask creditCleaner = new CreditHolderCleaner();
                Timer timer = new Timer(true);
                long hour = 1*60*60*1000L;
                long halfDay = 12*60*60*1000L;
                long month = 30*24*60*60*1000L;

                timer.scheduleAtFixedRate(checker, hour, hour);
                timer.scheduleAtFixedRate(creditCleaner, hour, hour);
                timer.scheduleAtFixedRate(curRates, halfDay, halfDay);
                timer.scheduleAtFixedRate(payment, month, month);


                SimpleUser bank = creationService.addUserForInitialize();
                saverService.saveUser(bank);
                for (DebitAccount account : bank.getAccounts()) {
                    saverService.saveAccount(account);
                }
            }
        };
    }


}

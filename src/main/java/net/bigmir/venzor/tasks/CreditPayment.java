package net.bigmir.venzor.tasks;

import net.bigmir.venzor.services.AccountService;

import java.util.TimerTask;

public class CreditPayment extends TimerTask {
    private AccountService accountService;

    public CreditPayment(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run() {
        accountService.creditPay();
    }
}

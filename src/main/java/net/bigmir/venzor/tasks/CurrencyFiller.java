package net.bigmir.venzor.tasks;

import net.bigmir.venzor.repositories.CurrencyRepository;
import net.bigmir.venzor.simpleclass.CurrencyJson;

import java.util.TimerTask;

public class CurrencyFiller extends TimerTask {
    private  CurrencyRepository currencyRepository;

    public CurrencyFiller(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }


    @Override
    public void run() {
        currencyRepository.saveAll(CurrencyJson.initCurrencies());
    }
}

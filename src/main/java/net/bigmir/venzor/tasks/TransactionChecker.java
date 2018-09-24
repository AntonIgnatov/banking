package net.bigmir.venzor.tasks;

import net.bigmir.venzor.entities.SimpleTransaction;
import net.bigmir.venzor.services.TransactionService;
import net.bigmir.venzor.enums.TransactionStatus;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class TransactionChecker extends TimerTask {
    private TransactionService transactionService;

    public TransactionChecker(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void run() {
        List<SimpleTransaction> listToSave = new LinkedList<>();
        List<SimpleTransaction> list = transactionService.findUnconfirmed();
        if (list.size() != 0) {
            listToSave.clear();
            long curDate = new Date().getTime();
            for (SimpleTransaction tr : list) {
                if ((curDate - tr.getDate().getTime()) >= 3600000L) {
                    tr.setStatus(TransactionStatus.CANCELED);
                    listToSave.add(tr);
                }
            }
        }
        transactionService.saveTransactions(listToSave);
    }
}

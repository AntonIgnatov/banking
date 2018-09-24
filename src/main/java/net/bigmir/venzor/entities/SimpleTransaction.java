package net.bigmir.venzor.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.bigmir.venzor.entities.accounts.BasicAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.enums.TransactionStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class SimpleTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long cardTo;
    private long accountTo;
    private long cardFrom;
    private long accountFrom;
    private double amountFrom;
    private double amountTo;
    private String currencyFrom;
    private String currencyTo;
    private Date date;
    private String description;

    @OneToOne(fetch = FetchType.LAZY, optional = false, mappedBy = "transaction", cascade = CascadeType.MERGE)
    private ConfirmSMSTransaction confirmSMSTransaction;

    @Enumerated
    private TransactionStatus status;


    public <T extends BasicAccount> SimpleTransaction(long cardFrom, T accountFrom, long cardTo, long accTo, double amount) {
        this.cardTo = cardTo;
        this.accountTo = accTo;
        this.cardFrom = cardFrom;
        this.accountFrom = accountFrom.getId();
        this.amountFrom = amount;
        this.currencyFrom = accountFrom.getCurrency();
        this.date = new Date();
        this.status = TransactionStatus.UNCONFIRMED;
        if (cardFrom != 0) {
            this.description = "Переказ з картки " + cardFrom + " на картку " + cardTo;
        } else {
            this.description = "Переказ з рахунку " + accountFrom + " на рахунок " + accTo;
        }
    }


    public <T extends BasicAccount> SimpleTransaction(T accountFrom, DebitAccount bank, double amount) {
        this.accountFrom = accountFrom.getId();
        this.amountFrom = amount;
        this.accountTo = bank.getId();
        this.amountTo = amount;
        this.date = new Date();
        this.currencyFrom = this.currencyTo = accountFrom.getCurrency();
        this.status = TransactionStatus.OK;
        this.description = "банківські послуги";
    }

    public <T extends BasicAccount> SimpleTransaction(T accountTo, long cardTo, double amount) {
        this.cardTo = cardTo;
        this.accountTo = accountTo.getId();
        this.amountTo = amount;
        this.date = new Date();
        this.currencyTo = accountTo.getCurrency();
        this.status = TransactionStatus.OK;
        this.description = "Поповнення рахунку";
    }


}

package net.bigmir.venzor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bigmir.venzor.entities.SimpleTransaction;

import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
public class TransactionDTO implements Comparable<TransactionDTO> {
    private Long id;
    private long to;
    private long from;
    private String description;
    private String date;
    private String amount;
    private String currency;
    private String status;

    public TransactionDTO basisTransaction(SimpleTransaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        this.date = sdf.format(transaction.getDate());
        this.status = transaction.getStatus().toString();
        return this;
    }

    public TransactionDTO forAccount(SimpleTransaction transaction) {
        this.basisTransaction(transaction);
        this.to = transaction.getAccountTo();
        this.from = transaction.getAccountFrom();
        return this;
    }

    public TransactionDTO from(SimpleTransaction transaction) {
        this.amount = String.format("%.2f", transaction.getAmountFrom());
        this.currency = transaction.getCurrencyFrom();
        return this;
    }
    public TransactionDTO to(SimpleTransaction transaction) {
        this.amount = String.format("%.2f", transaction.getAmountTo());
        this.currency = transaction.getCurrencyTo();
        return this;
    }

    public TransactionDTO forCard(SimpleTransaction transaction) {
        this.basisTransaction(transaction);
        this.to = transaction.getCardTo();
        this.from = transaction.getCardFrom();
        return this;
    }


    @Override
    public int compareTo(TransactionDTO o) {
        return this.getId().compareTo(o.getId());
    }
}

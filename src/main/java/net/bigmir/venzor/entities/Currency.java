package net.bigmir.venzor.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currency_rate")
@Data
@NoArgsConstructor
public class Currency implements Comparable<Currency> {
    @Id
    @Column(unique = true)
    private String name;
    private double rateBuy;
    private double rateSale;


    public Currency(String name, double rateBuy, double rateSale) {
        this.name = name;
        this.rateBuy = rateBuy;
        this.rateSale = rateSale;
    }

    @Override
    public int compareTo(Currency o) {
       return this.getName().compareTo(o.getName());
    }
}

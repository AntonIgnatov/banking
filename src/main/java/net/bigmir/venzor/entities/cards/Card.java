package net.bigmir.venzor.entities.cards;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bigmir.venzor.entities.accounts.BasicAccount;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Card<T extends BasicAccount> {
    @Id
    @Column(unique = true)
    private long id;
    @ManyToOne
    @JoinColumn
    private T account;
    private int mounth;
    private int year;
    private int cvv = 0;


    public Card(T account) {
        this.id = codeGeneration();
        this.account = account;
        this.mounth = Calendar.getInstance().get(Calendar.MONTH);
        this.year = Calendar.getInstance().get(Calendar.YEAR) + 2 - 2000;
        while (this.cvv < 100) {
            this.cvv = (int) (Math.random() * 1000);
        }
    }

    public long codeGeneration() {
        String[] masterCodes = new String[]{"51", "52", "53", "54", "55"};
        String code = masterCodes[new Double(Math.floor(Math.random() * masterCodes.length)).intValue()];
        for (int i = 0; i < 13; i++) {
            code += (int) (10 * Math.random());
        }
        int sum = 0;
        for (int i = 0; i < code.length(); i++) {
            int a = Integer.parseInt(String.valueOf(code.charAt(i)));
            if (i % 2 == 0) {
                sum += a;
            } else {
                if (a * 2 < 9) {
                    sum += a;
                } else {
                    sum += a * 2 - 9;
                }
            }
        }
        code += 10 - sum % 10;
        return Long.valueOf(code);
    }

    @Override
    public String toString() {
        return "Card № " + this.id + ", CVV " + this.cvv + ", valid till " + this.mounth + "/" + this.year;
    }
}

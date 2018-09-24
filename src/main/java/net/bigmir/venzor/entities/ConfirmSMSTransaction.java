package net.bigmir.venzor.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "confirm_sms_transaction")
@Data
@NoArgsConstructor
public class ConfirmSMSTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private SimpleTransaction transaction;
    @Column(unique = true)
    private int code;
    private Date date;

    public ConfirmSMSTransaction(int code) {
        this.code = code;
        this.date = new Date();
    }

}

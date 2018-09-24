package net.bigmir.venzor.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.bigmir.venzor.dto.SimpleUserDTO;
import net.bigmir.venzor.entities.accounts.CreditAccount;
import net.bigmir.venzor.entities.accounts.DebitAccount;
import net.bigmir.venzor.enums.UserRole;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class SimpleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(unique = true, nullable = false)
    private String phone;
    @Column(nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String ipn;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String addres;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<DebitAccount> accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<CreditAccount> credits;

    public SimpleUser(String name, String surname, String addres, String phone, String ipn, String passwordHash, String email) {
        this.name = name;
        this.surname = surname;
        this.addres = addres;
        this.phone = phone;
        this.ipn = ipn;
        this.email = email;
        this.password = passwordHash;
        this.role = UserRole.USER;
        accounts = new ArrayList<>();
        credits = new ArrayList<>();
    }

    public void addDebitAccount(DebitAccount account) {
        if(this.accounts==null){
            this.accounts = new ArrayList<>();
        }
        this.accounts.add(account);
    }

    public void addCredit(CreditAccount credit) {
        this.credits.add(credit);
    }

    public SimpleUser fromDTO(SimpleUserDTO user) {
//        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.phone = user.getPhone();

        this.password = user.getPassword();
        this.addres = user.getAddres();
        return this;

    }

    public DebitAccount getDebitAccountByCurrency(String currency){
        for(DebitAccount account : this.accounts){
            if(account.getCurrency().equals(currency)){
                return account;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleUser user = (SimpleUser) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(email, user.email) &&
                Objects.equals(ipn, user.ipn) &&
                Objects.equals(password, user.password) &&
                Objects.equals(addres, user.addres) &&
                role == user.role;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, surname, phone, email, ipn, password, addres);
    }
}
